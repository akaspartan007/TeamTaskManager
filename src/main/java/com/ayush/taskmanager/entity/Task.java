package com.ayush.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDate deadline;

    @ManyToOne
    private Project project;

    @ManyToOne
    private User assignedTo;
}