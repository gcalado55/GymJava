package com.treinoapp.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "workout")
@Getter
@Setter
@NoArgsConstructor
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String status = "IN_PROGRESS";

    @Column(name = "is_template", nullable = false)
    private boolean isTemplate = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutExercise> exercises = new ArrayList<>();
}