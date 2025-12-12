package com.opr3.opr3.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateRequest {
    private String title;
    private String description;
    private Date dueDate;
}
