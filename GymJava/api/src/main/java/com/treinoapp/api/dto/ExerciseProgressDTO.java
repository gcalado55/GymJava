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
        Double estimated1Rm,
        Double progressPct,
        List<ProgressPointDTO> points,
        List<SessionSummaryDTO> sessions
) {
}