package com.projectTracker.projectTracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.projectTracker.projectTracker.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer>{
    List<Project> findByUserUid(String userUid);
}
