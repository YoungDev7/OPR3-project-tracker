package com.opr3.opr3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.opr3.opr3.dto.AddUserToProjectRequest;
import com.opr3.opr3.dto.ProjectCreateRequest;
import com.opr3.opr3.dto.ProjectResponse;
import com.opr3.opr3.dto.ProjectUpdateRequest;
import com.opr3.opr3.dto.TaskResponse;
import com.opr3.opr3.entity.Project;
import com.opr3.opr3.entity.User;
import com.opr3.opr3.exception.ForbiddenException;
import com.opr3.opr3.repository.ProjectRepository;
import com.opr3.opr3.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final AuthUtilService authUtilService;

    private void verifyUserInProject(Project project, User user) {
        if (!project.getUsers().contains(user)) {
            throw new ForbiddenException("Access denied");
        }
    }

    public ProjectResponse createProject(ProjectCreateRequest request)
            throws IllegalArgumentException, AuthenticationException {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Project title cannot be blank");
        }

        User user = authUtilService.getAuthenticatedUser();

        Project project = Project.builder()
                .owner(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .isArchived(false)
                .build();

        project.getUsers().add(user);

        Project savedProject = projectRepository.save(project);
        return convertToResponse(savedProject);
    }

    public ProjectResponse getProjectById(Integer projectId) throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        verifyUserInProject(project, user);

        return convertToResponse(project);
    }

    public List<ProjectResponse> getAllUserProjects() throws AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        List<Project> projects = projectRepository.findByUsersUid(user.getUid());

        return projects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public ProjectResponse updateProject(Integer projectId, ProjectUpdateRequest request)
            throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        verifyUserInProject(project, user);

        if (project.getIsArchived()) {
            throw new IllegalArgumentException("Cannot update archived project");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Project title cannot be blank");
        }

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setDueDate(request.getDueDate());

        Project updatedProject = projectRepository.save(project);
        return convertToResponse(updatedProject);
    }

    public ProjectResponse archiveProject(Integer projectId) throws IllegalArgumentException, AuthenticationException {
        User user = authUtilService.getAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        verifyUserInProject(project, user);

        if (project.getIsArchived()) {
            throw new IllegalArgumentException("Project is already archived");
        }

        project.setIsArchived(true);
        Project archivedProject = projectRepository.save(project);
        return convertToResponse(archivedProject);
    }

    public void addUserToProject(Integer projectId, AddUserToProjectRequest request)
            throws IllegalArgumentException, AuthenticationException {
        User currentUser = authUtilService.getAuthenticatedUser();

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        verifyUserInProject(project, currentUser);

        User userToAdd = userRepository.findUserByEmail(request.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        project.getUsers().add(userToAdd);
        Project updatedProject = projectRepository.save(project);
    }

    private ProjectResponse convertToResponse(Project project) {
        List<TaskResponse> taskResponses = project.getTasks().stream()
                .map(task -> TaskResponse.builder()
                        .id(task.getId())
                        .projectId(project.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .dueDate(task.getDueDate())
                        .createdAt(task.getCreatedAt())
                        .updatedAt(task.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());

        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .dueDate(project.getDueDate())
                .isArchived(project.getIsArchived())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .tasks(taskResponses)
                .build();
    }
}
