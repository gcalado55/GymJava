package com.treinoapp.api.dto;

import java.util.List;
import java.util.Map;

public record DashboardStatsDTO(
        Integer workoutsThisMonth,
        Double totalVolumeKg,
        Double averageLoadKg,
        Double overallProgressPct,
        List<PriorityExerciseDTO> priorityExercises,
        Map<String, Integer> weeklyVolumePerMuscle,
        Map<String, Double> averageLoadPerExercise
) {
}