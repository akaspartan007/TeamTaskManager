package com.ayush.taskmanager.controller;

import com.ayush.taskmanager.entity.Task;
import com.ayush.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    @GetMapping
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody Task task) {

        if (task.getTitle() == null || task.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Task title is required");
        }

        Task saved = taskRepository.save(task);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody Task updated) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setStatus(updated.getStatus());
        return taskRepository.save(task);
    }

    @GetMapping("/user/{userId}")
    public List<Task> getTasksByUser(@PathVariable Long userId) {
        return taskRepository.findAll()
                .stream()
                .filter(t -> t.getAssignedTo() != null &&
                        t.getAssignedTo().getId().equals(userId))
                .toList();
    }
}