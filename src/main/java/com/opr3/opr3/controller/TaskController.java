package com.opr3.opr3.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<TaskResponse> createTask(@PathVariable Integer projectId,
            @RequestBody TaskCreateRequest request) {

        TaskResponse response = taskService.createTask(projectId, request);
        log.info("[{}] task created in project {}: task id {}", 201, projectId, response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Integer projectId, @PathVariable Integer taskId) {
        TaskResponse response = taskService.getTaskById(taskId);
        log.info("[{}] task retrieved: {}", 200, taskId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getProjectTasks(@PathVariable Integer projectId) {
        List<TaskResponse> response = taskService.getProjectTasks(projectId);
        log.info("[{}] tasks retrieved for project {}: {} tasks", 200, projectId, response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TaskResponse>> getProjectTasksPaginated(@PathVariable Integer projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TaskResponse> response = taskService.getProjectTasksPaginated(projectId, page, size);
        log.info("[{}] paginated tasks retrieved for project {}: page {}, size {}", 200, projectId, page, size);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Integer projectId, @PathVariable Integer taskId,
            @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        log.info("[{}] task updated: {}", 200, taskId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(@PathVariable Integer projectId, @PathVariable Integer taskId,
            @RequestBody TaskStatusUpdateRequest request) {
        TaskResponse response = taskService.updateTaskStatus(taskId, request);
        log.info("[{}] task status updated: {} to {}", 200, taskId, request.getStatus());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer projectId, @PathVariable Integer taskId) {
        taskService.deleteTask(taskId);
        log.info("[{}] task deleted: {}", 204, taskId);
        return ResponseEntity.noContent().build();
    }
}
