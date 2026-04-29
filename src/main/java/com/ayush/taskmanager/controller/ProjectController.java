package com.ayush.taskmanager.controller;

import com.ayush.taskmanager.entity.Project;
import com.ayush.taskmanager.entity.User;
import com.ayush.taskmanager.repository.ProjectRepository;
import com.ayush.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository; // ✅ FIX ADDED

    @GetMapping
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProject(@RequestBody Project project) {

        // ✅ VALIDATION
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Project name is required");
        }

        Project saved = projectRepository.save(project);
        return ResponseEntity.ok(saved);
    }

    // ➕ Add member
    @PutMapping("/{projectId}/add-member/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Project addMember(@PathVariable Long projectId, @PathVariable Long userId) {

        Project project = projectRepository.findById(projectId).orElseThrow();
        User user = userRepository.findById(userId).orElseThrow();

        // ✅ Prevent duplicate
        boolean exists = project.getMembers()
                .stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (!exists) {
            project.getMembers().add(user);
        }

        return projectRepository.save(project);
    }

    // ❌ Remove member
    @PutMapping("/{projectId}/remove-member/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Project removeMember(@PathVariable Long projectId, @PathVariable Long userId) {

        Project project = projectRepository.findById(projectId).orElseThrow();

        project.getMembers().removeIf(u -> u.getId().equals(userId));

        return projectRepository.save(project);
    }
}