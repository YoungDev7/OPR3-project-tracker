package com.opr3.opr3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.opr3.opr3.dto.TaskCreateRequest;
import com.opr3.opr3.dto.TaskResponse;
import com.opr3.opr3.dto.TaskStatusUpdateRequest;
import com.opr3.opr3.dto.TaskUpdateRequest;
import com.opr3.opr3.entity.Project;
import com.opr3.opr3.entity.Task;
import com.opr3.opr3.entity.Task.TaskStatus;
import com.opr3.opr3.entity.User;
import com.opr3.opr3.exception.ForbiddenException;
import com.opr3.opr3.repository.ProjectRepository;
import com.opr3.opr3.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final AuthUtilService authUtilService;

    private void verifyUserInProject(Project project, User user) {
        if (!project.getUsers().contains(user)) {
            throw new ForbiddenException("Access denied");
        }
    }

    public TaskResponse createTask(Integer projectId, TaskCreateRequest request)
            throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        verifyUserInProject(project, user);

        if (project.getIsArchived()) {
            throw new IllegalArgumentException("Cannot add task to archived project");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title cannot be blank");
        }

        TaskStatus status = request.getStatus() != null ? request.getStatus() : TaskStatus.TODO;

        Task task = Task.builder()
                .project(project)
                .title(request.getTitle())
                .description(request.getDescription())
                .status(status)
                .dueDate(request.getDueDate())
                .build();

        Task savedTask = taskRepository.save(task);
        return convertToResponse(savedTask);
    }

    public TaskResponse getTaskById(Integer taskId) throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        verifyUserInProject(task.getProject(), user);

        return convertToResponse(task);
    }

    public List<TaskResponse> getProjectTasks(Integer projectId)
            throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        verifyUserInProject(project, user);

        return project.getTasks().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Page<TaskResponse> getProjectTasksPaginated(Integer projectId, int page, int size)
            throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        verifyUserInProject(project, user);

        Pageable pageable = PageRequest.of(page, size);
        Page<Task> taskPage = taskRepository.findByProjectId(projectId, pageable);

        return taskPage.map(this::convertToResponse);
    }

    public TaskResponse updateTask(Integer taskId, TaskUpdateRequest request)
            throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        verifyUserInProject(task.getProject(), user);

        if (task.getProject().getIsArchived()) {
            throw new IllegalArgumentException("Cannot update task in archived project");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title cannot be blank");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }

        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    public TaskResponse updateTaskStatus(Integer taskId, TaskStatusUpdateRequest request)
            throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        verifyUserInProject(task.getProject(), user);

        if (task.getProject().getIsArchived()) {
            throw new IllegalArgumentException("Cannot update task status in archived project");
        }

        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Task status cannot be null");
        }

        task.setStatus(request.getStatus());
        Task updatedTask = taskRepository.save(task);
        return convertToResponse(updatedTask);
    }

    public void deleteTask(Integer taskId) throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        verifyUserInProject(task.getProject(), user);

        if (task.getProject().getIsArchived()) {
            throw new IllegalArgumentException("Cannot delete task from archived project");
        }

        taskRepository.delete(task);
    }

    private TaskResponse convertToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .projectId(task.getProject().getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
