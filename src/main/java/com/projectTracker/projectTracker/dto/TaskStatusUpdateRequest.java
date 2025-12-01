package com.projectTracker.projectTracker.dto;

import com.projectTracker.projectTracker.entity.Task.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateRequest {
    private TaskStatus status;
}
