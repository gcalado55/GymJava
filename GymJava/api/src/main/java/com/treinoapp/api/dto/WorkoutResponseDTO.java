package com.treinoapp.api.dto;

import java.time.Instant;
import java.util.List;

public record WorkoutResponseDTO(
        String name,
        String memberName,
        Instant createdAt,
        String status,
        boolean isTemplate,
        List<WorkoutExerciseDTO> exercises
) {
}