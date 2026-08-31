package com.treinoapp.api.dto;

import java.util.List;
import java.util.UUID;

public record ExerciseProgressDTO(
        UUID exerciseId,
        String exerciseName,
        Double latestWeightKg,
        Integer latestReps,
        Double bestWeightKg,
        Integer bestReps,
        Integer totalReps,
        Double progressPct,
        List<ProgressPointDTO> points,
        List<SessionSummaryDTO> sessions
) {
}