package com.treinoapp.api.repository;

import com.treinoapp.api.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExerciciseRepository extends JpaRepository<Workout, UUID> {
}