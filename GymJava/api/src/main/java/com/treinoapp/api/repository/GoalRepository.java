package com.treinoapp.api.repository;

import com.treinoapp.api.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByMember_Id(UUID memberId);
}