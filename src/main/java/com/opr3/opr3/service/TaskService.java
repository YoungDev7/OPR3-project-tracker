package com.opr3.opr3.service;

import java.util.List;
import java.util.stream.Collectors;

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
import com.opr3.opr3.repository.ProjectRepository;
import com.opr3.opr3.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final AuthService authService;

    /**
     * Creates a new task within a project for the authenticated user.
     * 
     * @param projectId the ID of the project to add the task to
     * @param request the task creation request containing title, description, due date, and status
     * @return TaskResponse containing the created task details
     * @throws IllegalArgumentException if project not found, doesn't belong to user, is archived, or title is blank
     * @throws AuthenticationException if user is not authenticated
     */
    public TaskResponse createTask(Integer projectId, TaskCreateRequest request) 
            throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Project not found");
        }

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

    /**
     * Retrieves a task by its ID, ensuring the associated project belongs to the authenticated user.
     * 
     * @param taskId the ID of the task to retrieve
     * @return TaskResponse containing the task details
     * @throws IllegalArgumentException if task not found or doesn't belong to user's project
     * @throws AuthenticationException if user is not authenticated
     */
    public TaskResponse getTaskById(Integer taskId) throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getProject().getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Task not found");
        }

        return convertToResponse(task);
    }

    /**
     * Retrieves all tasks for a specific project belonging to the authenticated user.
     * 
     * @param projectId the ID of the project
     * @return List of TaskResponse containing all project tasks
     * @throws IllegalArgumentException if project not found or doesn't belong to user
     * @throws AuthenticationException if user is not authenticated
     */
    public List<TaskResponse> getProjectTasks(Integer projectId) 
            throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Project not found");
        }

        return project.getTasks().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing task within a project.
     * 
     * @param taskId the ID of the task to update
     * @param request the task update request containing updated fields
     * @return TaskResponse containing the updated task details
     * @throws IllegalArgumentException if task not found, doesn't belong to user's project, project is archived, or title is blank
     * @throws AuthenticationException if user is not authenticated
     */
    public TaskResponse updateTask(Integer taskId, TaskUpdateRequest request) 
            throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getProject().getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Task not found");
        }

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

    /**
     * Updates the status of a task (e.g., TODO, IN_PROGRESS, DONE).
     * 
     * @param taskId the ID of the task to update
     * @param request the status update request containing the new status
     * @return TaskResponse containing the updated task details
     * @throws IllegalArgumentException if task not found, doesn't belong to user's project, project is archived, or status is null
     * @throws AuthenticationException if user is not authenticated
     */
    public TaskResponse updateTaskStatus(Integer taskId, TaskStatusUpdateRequest request) 
            throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getProject().getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Task not found");
        }

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

    /**
     * Deletes a task from a project.
     * 
     * @param taskId the ID of the task to delete
     * @throws IllegalArgumentException if task not found, doesn't belong to user's project, or project is archived
     * @throws AuthenticationException if user is not authenticated
     */
    public void deleteTask(Integer taskId) throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (!task.getProject().getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Task not found");
        }

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
