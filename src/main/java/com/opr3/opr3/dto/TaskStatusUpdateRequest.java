package com.opr3.opr3.dto;

import com.opr3.opr3.entity.Task.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusUpdateRequest {
    private TaskStatus status;
}
