package com.treinoapp.api.dto;

import java.util.List;

public record DashboardStatsDTO(
        Integer workoutsThisMonth,
        Double totalVolumeKg,
        Double averageLoadKg,
        Double overallProgressPct,
        List<PriorityExerciseDTO> priorityExercises,
        Integer setsLoggedThisWeek
) {
}