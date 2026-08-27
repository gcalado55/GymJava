package com.treinoapp.api.dto;

import java.util.List;

public record WorkoutExerciseDTO(
        String exerciseName,
        String notes,
        String technique,
        String executionGuidance,
        List<WorkoutSetDTO> sets
) {
}