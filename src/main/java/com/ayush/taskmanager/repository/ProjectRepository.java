package com.ayush.taskmanager.repository;

import com.ayush.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}