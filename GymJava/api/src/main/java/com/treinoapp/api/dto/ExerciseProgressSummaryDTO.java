package com.treinoapp.api.dto;

import java.util.List;

public record ExerciseProgressSummaryDTO(
        String exerciseName,
        Double progressPct,
        List<Double> points
) {
}