package com.treinoapp.api.dto;

import java.util.List;

import java.util.UUID;

public record WorkoutExerciseDTO(
        UUID id,
        String exerciseName,
        String targetMuscles,
        String notes,
        String logNotes,
        String technique,
        String techniqueName,
        int weeklyVolume,
        List<WorkoutSetDTO> sets
) {
}