package com.ayush.taskmanager.controller;

import com.ayush.taskmanager.entity.*;
import com.ayush.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TaskRepository taskRepository;

    @GetMapping
    public Map<String, Object> getDashboard() {

        List<Task> tasks = taskRepository.findAll();

        long todo = tasks.stream()
                .filter(t -> t.getStatus() == Status.TODO)
                .count();

        long inProgress = tasks.stream()
                .filter(t -> t.getStatus() == Status.IN_PROGRESS)
                .count();

        long done = tasks.stream()
                .filter(t -> t.getStatus() == Status.DONE)
                .count();

        long overdue = tasks.stream()
                .filter(t -> t.getDeadline() != null &&
                        t.getDeadline().isBefore(LocalDate.now()))
                .count();

        Map<String, Object> data = new HashMap<>();
        data.put("totalTasks", tasks.size());
        data.put("todo", todo);
        data.put("inProgress", inProgress);
        data.put("done", done);
        data.put("overdue", overdue);

        return data;
    }
}