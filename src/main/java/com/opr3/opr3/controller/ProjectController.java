package com.opr3.opr3.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opr3.opr3.dto.AddUserToProjectRequest;
import com.opr3.opr3.dto.ProjectCreateRequest;
import com.opr3.opr3.dto.ProjectResponse;
import com.opr3.opr3.dto.ProjectUpdateRequest;
import com.opr3.opr3.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@Tag(name = "Projects", description = "Project management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;

    @Operation(summary = "Create a new project", description = "Creates a new project with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Project created successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectCreateRequest request) {
        ProjectResponse response = projectService.createProject(request);
        log.info("[{}] project created: {}", 201, response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get project by ID", description = "Retrieves a specific project by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Integer projectId) {
        ProjectResponse response = projectService.getProjectById(projectId);
        log.info("[{}] project retrieved: {}", 200, projectId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all user projects", description = "Retrieves all projects accessible by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllUserProjects() {
        List<ProjectResponse> response = projectService.getAllUserProjects();
        log.info("[{}] all user projects retrieved: {} projects", 200, response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update project", description = "Updates an existing project with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Integer projectId,
            @RequestBody ProjectUpdateRequest request) {
        ProjectResponse response = projectService.updateProject(projectId, request);
        log.info("[{}] project updated: {}", 200, projectId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Archive project", description = "Marks a project as archived")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project archived successfully", content = @Content(schema = @Schema(implementation = ProjectResponse.class))),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PatchMapping("/{projectId}/archive")
    public ResponseEntity<ProjectResponse> archiveProject(@PathVariable Integer projectId) {
        ProjectResponse response = projectService.archiveProject(projectId);
        log.info("[{}] project archived: {}", 200, projectId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Add user to project", description = "Adds a user to a project by username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User added successfully"),
            @ApiResponse(responseCode = "404", description = "Project or user not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping("/{projectId}/users")
    public ResponseEntity<?> addUserToProject(@PathVariable Integer projectId,
            @RequestBody AddUserToProjectRequest request) {
        projectService.addUserToProject(projectId, request);
        log.info("[{}] user added to project: {}", 200, projectId);
        return ResponseEntity.ok().build();
    }
}
