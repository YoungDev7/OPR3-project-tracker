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

import com.opr3.opr3.dto.ProjectCreateRequest;
import com.opr3.opr3.dto.ProjectResponse;
import com.opr3.opr3.dto.ProjectUpdateRequest;
import com.opr3.opr3.service.ProjectService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private static final Logger log = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectCreateRequest request) {
        ProjectResponse response = projectService.createProject(request);
        log.info("[{}] project created: {}", 201, response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Integer projectId) {
        ProjectResponse response = projectService.getProjectById(projectId);
        log.info("[{}] project retrieved: {}", 200, projectId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllUserProjects() {
        List<ProjectResponse> response = projectService.getAllUserProjects();
        log.info("[{}] all user projects retrieved: {} projects", 200, response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Integer projectId,
            @RequestBody ProjectUpdateRequest request) {
        ProjectResponse response = projectService.updateProject(projectId, request);
        log.info("[{}] project updated: {}", 200, projectId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{projectId}/archive")
    public ResponseEntity<ProjectResponse> archiveProject(@PathVariable Integer projectId) {
        ProjectResponse response = projectService.archiveProject(projectId);
        log.info("[{}] project archived: {}", 200, projectId);
        return ResponseEntity.ok(response);
    }
}
