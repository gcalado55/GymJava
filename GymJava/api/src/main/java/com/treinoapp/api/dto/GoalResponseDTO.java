package com.treinoapp.api.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GoalResponseDTO(
        UUID id,
        UUID exerciseId,
        String exerciseName,
        Double targetWeightKg,
        Integer targetReps,
        LocalDate targetDate,
        Instant createdAt,
        Double currentWeightKg,
        Integer currentReps
) {
}