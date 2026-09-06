package com.treinoapp.api.dto;

import java.util.List;

public record PriorityExerciseDTO(
        String exerciseName,
        String muscleGroup,
        Double volumeKg,
        Double progressPct,
        List<Double> points
) {
}   