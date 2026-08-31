package com.treinoapp.api.repository;

import com.treinoapp.api.model.WorkoutExercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutExerciseRepository extends JpaRepository<WorkoutExercise, UUID> {
    List<WorkoutExercise> findByExercise_IdAndWorkout_Member_Id(UUID exerciseId, UUID memberId);
}