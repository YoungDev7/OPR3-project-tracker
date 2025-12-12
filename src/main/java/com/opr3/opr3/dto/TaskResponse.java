package com.opr3.opr3.dto;

import java.time.LocalDateTime;
import java.util.Date;

import com.opr3.opr3.entity.Task.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {
    private Integer id;
    private Integer projectId;
    private String title;
    private String description;
    private TaskStatus status;
    private Date dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
