package com.opr3.opr3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.opr3.opr3.entity.Project;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Integer> {
    @Query("SELECT p FROM Project p JOIN p.users u WHERE u.uid = :userUid")
    List<Project> findByUsersUid(@Param("userUid") String userUid);
}
