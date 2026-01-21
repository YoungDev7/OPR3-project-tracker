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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management endpoints within projects")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Creates a new task within a specific project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @Parameter(description = "Project ID") @PathVariable Integer projectId,
            @RequestBody TaskCreateRequest request) {

        TaskResponse response = taskService.createTask(projectId, request);
        log.info("[{}] task created in project {}: task id {}", 201, projectId, response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task retrieved successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "Project ID") @PathVariable Integer projectId,
            @Parameter(description = "Task ID") @PathVariable Integer taskId) {
        TaskResponse response = taskService.getTaskById(taskId);
        log.info("[{}] task retrieved: {}", 200, taskId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all project tasks", description = "Retrieves all tasks for a specific project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getProjectTasks(
            @Parameter(description = "Project ID") @PathVariable Integer projectId) {
        List<TaskResponse> response = taskService.getProjectTasks(projectId);
        log.info("[{}] tasks retrieved for project {}: {} tasks", 200, projectId, response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get paginated project tasks", description = "Retrieves tasks for a project with pagination support")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Project not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @GetMapping("/paginated")
    public ResponseEntity<Page<TaskResponse>> getProjectTasksPaginated(
            @Parameter(description = "Project ID") @PathVariable Integer projectId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        Page<TaskResponse> response = taskService.getProjectTasksPaginated(projectId, page, size);
        log.info("[{}] paginated tasks retrieved for project {}: page {}, size {}", 200, projectId, page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update task", description = "Updates an existing task with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Project ID") @PathVariable Integer projectId,
            @Parameter(description = "Task ID") @PathVariable Integer taskId,
            @RequestBody TaskUpdateRequest request) {
        TaskResponse response = taskService.updateTask(taskId, request);
        log.info("[{}] task updated: {}", 200, taskId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update task status", description = "Updates only the status of an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status updated successfully", content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @PatchMapping("/{taskId}/status")
    public ResponseEntity<TaskResponse> updateTaskStatus(
            @Parameter(description = "Project ID") @PathVariable Integer projectId,
            @Parameter(description = "Task ID") @PathVariable Integer taskId,
            @RequestBody TaskStatusUpdateRequest request) {
        TaskResponse response = taskService.updateTaskStatus(taskId, request);
        log.info("[{}] task status updated: {} to {}", 200, taskId, request.getStatus());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete task", description = "Permanently deletes a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "Project ID") @PathVariable Integer projectId,
            @Parameter(description = "Task ID") @PathVariable Integer taskId) {
        taskService.deleteTask(taskId);
        log.info("[{}] task deleted: {}", 204, taskId);
        return ResponseEntity.noContent().build();
    }
}
