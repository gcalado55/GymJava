package com.treinoapp.api.repository;

import com.treinoapp.api.model.Workout;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkoutRepository extends JpaRepository<Workout, UUID> {
    List<Workout> findAllByOrderByCreatedAtDesc();
    List<Workout> findByMemberIdOrderByCreatedAtDesc(UUID memberId);
}