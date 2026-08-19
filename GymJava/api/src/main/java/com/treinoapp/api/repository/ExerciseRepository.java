package com.treinoapp.api.repository;

import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
}