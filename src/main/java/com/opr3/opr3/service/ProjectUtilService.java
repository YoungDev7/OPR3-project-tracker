package com.opr3.opr3.service;

import org.springframework.stereotype.Service;

import com.opr3.opr3.entity.Project;
import com.opr3.opr3.entity.User;
import com.opr3.opr3.exception.ForbiddenException;

@Service
public class ProjectUtilService {

    public void verifyUserInProject(Project project, User user) {
        if (!project.getUsers().contains(user)) {
            throw new ForbiddenException("Access denied");
        }
    }
}
