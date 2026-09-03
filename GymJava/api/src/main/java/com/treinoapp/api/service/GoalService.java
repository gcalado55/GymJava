package com.treinoapp.api.service;

import com.treinoapp.api.dto.GoalResponseDTO;
import com.treinoapp.api.exception.ExerciseNotFoundException;
import com.treinoapp.api.exception.GoalNotFoundException;
import com.treinoapp.api.exception.MemberNotFoundException;
import com.treinoapp.api.model.*;
import com.treinoapp.api.repository.ExerciseRepository;
import com.treinoapp.api.repository.GoalRepository;
import com.treinoapp.api.repository.MemberRepository;
import com.treinoapp.api.repository.WorkoutExerciseRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private static final Duration ACHIEVED_RETENTION = Duration.ofDays(3);

    private final GoalRepository goalRepository;
    private final MemberRepository memberRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public GoalService(GoalRepository goalRepository,
                       MemberRepository memberRepository,
                       ExerciseRepository exerciseRepository,
                       WorkoutExerciseRepository workoutExerciseRepository) {
        this.goalRepository = goalRepository;
        this.memberRepository = memberRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    public GoalResponseDTO create(UUID memberId, UUID exerciseId, Double targetWeightKg,
                                  Integer targetReps, LocalDate targetDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException(exerciseId));

        Goal goal = new Goal();
        goal.setMember(member);
        goal.setExercise(exercise);
        goal.setTargetWeightKg(targetWeightKg);
        goal.setTargetReps(targetReps);
        goal.setTargetDate(targetDate);

        return toDTO(goalRepository.save(goal));
    }

    public List<GoalResponseDTO> findAllByMember(UUID memberId) {
        cleanupStaleAchieved();
        return goalRepository.findByMember_Id(memberId).stream()
                .map(this::toDTO)
                .toList();
    }

    public GoalResponseDTO findById(UUID id) {
        cleanupStaleAchieved();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));
        return toDTO(goal);
    }

    public void delete(UUID id) {
        if (!goalRepository.existsById(id)) {
            throw new GoalNotFoundException(id);
        }
        goalRepository.deleteById(id);
    }

    // Auto-delete achieved goals after 3 days of being reached.
    @Scheduled(fixedDelay = 3600000) // run every hour
    public void cleanupStaleAchieved() {
        Instant cutoff = Instant.now().minus(ACHIEVED_RETENTION);
        List<Goal> goals = goalRepository.findAll();
        for (Goal goal : goals) {
            if (goal.getReachedAt() != null && goal.getReachedAt().isBefore(cutoff)) {
                goalRepository.delete(goal);
            }
        }
    }

    private GoalResponseDTO toDTO(Goal goal) {
        WorkoutSet latestSet = findLatestSet(goal.getExercise().getId(), goal.getMember().getId());

        // Detect and persist the moment the goal is reached.
        if (isReached(goal, latestSet)) {
            if (goal.getReachedAt() == null) {
                goal.setReachedAt(Instant.now());
                goalRepository.save(goal);
            }
        } else if (goal.getReachedAt() != null) {
            // If the user falls below the target, reset reachedAt (not yet "retained").
            goal.setReachedAt(null);
            goalRepository.save(goal);
        }

        return new GoalResponseDTO(
                goal.getId(),
                goal.getExercise().getId(),
                goal.getExercise().getName(),
                goal.getTargetWeightKg(),
                goal.getTargetReps(),
                goal.getTargetDate(),
                goal.getCreatedAt(),
                latestSet != null ? latestSet.getWeightKg() : null,
                latestSet != null ? latestSet.getReps() : null
        );
    }

    private boolean isReached(Goal goal, WorkoutSet latestSet) {
        if (goal.getTargetWeightKg() == null || goal.getTargetReps() == null) return false;
        if (latestSet == null) return false;
        double targetLoad = goal.getTargetWeightKg() * goal.getTargetReps();
        double currentLoad = latestSet.getWeightKg() * latestSet.getReps();
        return currentLoad >= targetLoad;
    }

    private WorkoutSet findLatestSet(UUID exerciseId, UUID memberId) {
        record TimedSet(WorkoutSet set, Instant date) {}

        return workoutExerciseRepository.findByExercise_IdAndWorkout_Member_Id(exerciseId, memberId).stream()
                .filter(we -> !we.getWorkout().isTemplate())
                .flatMap(we -> we.getSets().stream()
                        .map(s -> new TimedSet(s, we.getWorkout().getCreatedAt())))
                .max(Comparator.comparing(TimedSet::date))
                .map(TimedSet::set)
                .orElse(null);
    }
}
