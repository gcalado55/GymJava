package com.treinoapp.api.controller;

import com.treinoapp.api.dto.CreateGoalRequestDTO;
import com.treinoapp.api.dto.GoalResponseDTO;
import com.treinoapp.api.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalResponseDTO> create(@AuthenticationPrincipal UUID memberId, @Valid @RequestBody CreateGoalRequestDTO dto) {
        GoalResponseDTO goal = goalService.create(
                memberId, dto.exerciseId(), dto.targetWeightKg(), dto.targetReps(), dto.targetDate());
        return ResponseEntity.ok(goal);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponseDTO>> findAllByMember(@AuthenticationPrincipal UUID memberId) {
        return ResponseEntity.ok(goalService.findAllByMember(memberId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GoalResponseDTO> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(goalService.findById(id));
    }
}