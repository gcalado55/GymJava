package com.treinoapp.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "workout_set")
@Getter
@Setter
@NoArgsConstructor
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private WorkoutExercise workoutExercise;

    @Column(nullable = false)
    private Integer setNumber;

    @Column(nullable = false)
    private Integer reps;

    @Column(nullable = false)
    private Double weightKg;
}