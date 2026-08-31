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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

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
        return goalRepository.findByMember_Id(memberId).stream()
                .map(this::toDTO)
                .toList();
    }

    public GoalResponseDTO findById(UUID id) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException(id));
        return toDTO(goal);
    }

    private GoalResponseDTO toDTO(Goal goal) {
        WorkoutSet bestSet = findBestSet(goal.getExercise().getId(), goal.getMember().getId());

        return new GoalResponseDTO(
                goal.getId(),
                goal.getExercise().getId(),
                goal.getExercise().getName(),
                goal.getTargetWeightKg(),
                goal.getTargetReps(),
                goal.getTargetDate(),
                goal.getCreatedAt(),
                bestSet != null ? bestSet.getWeightKg() : null,
                bestSet != null ? bestSet.getReps() : null
        );
    }

    private WorkoutSet findBestSet(UUID exerciseId, UUID memberId) {
        List<WorkoutExercise> workoutExercises =
                workoutExerciseRepository.findByExercise_IdAndWorkout_Member_Id(exerciseId, memberId);

        return workoutExercises.stream()
                .flatMap(we -> we.getSets().stream())
                .max(Comparator.comparingDouble(WorkoutSet::getWeightKg))
                .orElse(null);
    }
}