package com.projectTracker.projectTracker.dto;

import java.util.Date;

import com.projectTracker.projectTracker.entity.Task.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskCreateRequest {
    private String title;
    private String description;
    private Date dueDate;
    private TaskStatus status;
}
