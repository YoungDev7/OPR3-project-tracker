package com.opr3.opr3.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
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
    public ResponseEntity<?> createProject(@RequestBody ProjectCreateRequest request) {
        try {
            ProjectResponse response = projectService.createProject(request);
            log.info("[{}] project created: {}", 201, response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] project creation failed: {}", 400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] project creation failed: {}", 401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] project creation failed: {}", 500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@PathVariable Integer projectId) {
        try {
            ProjectResponse response = projectService.getProjectById(projectId);
            log.info("[{}] project retrieved: {}", 200, projectId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] project retrieval failed: {}", 404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] project retrieval failed: {}", 401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] project retrieval failed: {}", 500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUserProjects() {
        try {
            List<ProjectResponse> response = projectService.getAllUserProjects();
            log.info("[{}] all user projects retrieved: {} projects", 200, response.size());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            log.warn("[{}] project list retrieval failed: {}", 401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] project list retrieval failed: {}", 500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateProject(@PathVariable Integer projectId, @RequestBody ProjectUpdateRequest request) {
        try {
            ProjectResponse response = projectService.updateProject(projectId, request);
            log.info("[{}] project updated: {}", 200, projectId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] project update failed: {}", 400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] project update failed: {}", 401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] project update failed: {}", 500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PatchMapping("/{projectId}/archive")
    public ResponseEntity<?> archiveProject(@PathVariable Integer projectId) {
        try {
            ProjectResponse response = projectService.archiveProject(projectId);
            log.info("[{}] project archived: {}", 200, projectId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] project archive failed: {}", 400, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] project archive failed: {}", 401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] project archive failed: {}", 500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
