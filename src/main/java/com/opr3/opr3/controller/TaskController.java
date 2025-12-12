package com.opr3.opr3.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opr3.opr3.dto.TaskCreateRequest;
import com.opr3.opr3.dto.TaskResponse;
import com.opr3.opr3.dto.TaskStatusUpdateRequest;
import com.opr3.opr3.dto.TaskUpdateRequest;
import com.opr3.opr3.service.TaskService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<?> createTask(@PathVariable Integer projectId, @RequestBody TaskCreateRequest request) {
        try {
            TaskResponse response = taskService.createTask(projectId, request);
            log.info("[{}] task created in project {}: task id {}", 201, projectId, response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] task creation failed in project {}: {}", 400, projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] task creation failed in project {}: {}", 401, projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] task creation failed in project {}: {}", 500, projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<?> getTaskById(@PathVariable Integer projectId, @PathVariable Integer taskId) {
        try {
            TaskResponse response = taskService.getTaskById(taskId);
            log.info("[{}] task retrieved: {}", 200, taskId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] task retrieval failed: {}", 404, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] task retrieval failed: {}", 401, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] task retrieval failed: {}", 500, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @GetMapping
    public ResponseEntity<?> getProjectTasks(@PathVariable Integer projectId) {
        try {
            List<TaskResponse> response = taskService.getProjectTasks(projectId);
            log.info("[{}] tasks retrieved for project {}: {} tasks", 200, projectId, response.size());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] task list retrieval failed for project {}: {}", 404, projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] task list retrieval failed for project {}: {}", 401, projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] task list retrieval failed for project {}: {}", 500, projectId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Integer projectId, @PathVariable Integer taskId,
            @RequestBody TaskUpdateRequest request) {
        try {
            TaskResponse response = taskService.updateTask(taskId, request);
            log.info("[{}] task updated: {}", 200, taskId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] task update failed for task {}: {}", 400, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] task update failed for task {}: {}", 401, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] task update failed for task {}: {}", 500, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Integer projectId, @PathVariable Integer taskId,
            @RequestBody TaskStatusUpdateRequest request) {
        try {
            TaskResponse response = taskService.updateTaskStatus(taskId, request);
            log.info("[{}] task status updated: {} to {}", 200, taskId, request.getStatus());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("[{}] task status update failed for task {}: {}", 400, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] task status update failed for task {}: {}", 401, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] task status update failed for task {}: {}", 500, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer projectId, @PathVariable Integer taskId) {
        try {
            taskService.deleteTask(taskId);
            log.info("[{}] task deleted: {}", 204, taskId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("[{}] task deletion failed for task {}: {}", 400, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (AuthenticationException e) {
            log.warn("[{}] task deletion failed for task {}: {}", 401, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            log.error("[{}] task deletion failed for task {}: {}", 500, taskId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }
}
