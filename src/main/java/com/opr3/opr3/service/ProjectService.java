package com.opr3.opr3.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.opr3.opr3.dto.ProjectCreateRequest;
import com.opr3.opr3.dto.ProjectResponse;
import com.opr3.opr3.dto.ProjectUpdateRequest;
import com.opr3.opr3.dto.TaskResponse;
import com.opr3.opr3.entity.Project;
import com.opr3.opr3.entity.User;
import com.opr3.opr3.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final AuthService authService;

    /**
     * Creates a new project for the authenticated user.
     * 
     * @param request the project creation request containing title, description, and due date
     * @return ProjectResponse containing the created project details
     * @throws IllegalArgumentException if the title is blank or null
     * @throws AuthenticationException if user is not authenticated
     */
    public ProjectResponse createProject(ProjectCreateRequest request) throws IllegalArgumentException, AuthenticationException {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Project title cannot be blank");
        }

        User user = authService.getAuthenticatedUser();

        Project project = Project.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .isArchived(false)
                .build();

        Project savedProject = projectRepository.save(project);
        return convertToResponse(savedProject);
    }

    /**
     * Retrieves a project by its ID, ensuring it belongs to the authenticated user.
     * 
     * @param projectId the ID of the project to retrieve
     * @return ProjectResponse containing the project details
     * @throws IllegalArgumentException if project not found or doesn't belong to user
     * @throws AuthenticationException if user is not authenticated
     */
    public ProjectResponse getProjectById(Integer projectId) throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Project not found");
        }

        return convertToResponse(project);
    }

    /**
     * Retrieves all projects belonging to the authenticated user.
     * 
     * @return List of ProjectResponse containing all user's projects
     * @throws AuthenticationException if user is not authenticated
     */
    public List<ProjectResponse> getAllUserProjects() throws AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        List<Project> projects = projectRepository.findByUserUid(user.getUid());

        return projects.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing project for the authenticated user.
     * 
     * @param projectId the ID of the project to update
     * @param request the project update request containing updated fields
     * @return ProjectResponse containing the updated project details
     * @throws IllegalArgumentException if project not found, doesn't belong to user, is archived, or title is blank
     * @throws AuthenticationException if user is not authenticated
     */
    public ProjectResponse updateProject(Integer projectId, ProjectUpdateRequest request) 
            throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Project not found");
        }

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

    /**
     * Archives a project for the authenticated user, making it read-only.
     * 
     * @param projectId the ID of the project to archive
     * @return ProjectResponse containing the archived project details
     * @throws IllegalArgumentException if project not found, doesn't belong to user, or is already archived
     * @throws AuthenticationException if user is not authenticated
     */
    public ProjectResponse archiveProject(Integer projectId) throws IllegalArgumentException, AuthenticationException {
        User user = authService.getAuthenticatedUser();
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (!project.getUser().getUid().equals(user.getUid())) {
            throw new IllegalArgumentException("Project not found");
        }

        if (project.getIsArchived()) {
            throw new IllegalArgumentException("Project is already archived");
        }

        project.setIsArchived(true);
        Project archivedProject = projectRepository.save(project);
        return convertToResponse(archivedProject);
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