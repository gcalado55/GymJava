package com.treinoapp.api.service;

import com.treinoapp.api.dto.WorkoutExerciseDTO;
import com.treinoapp.api.dto.WorkoutResponseDTO;
import com.treinoapp.api.dto.WorkoutSetDTO;
import com.treinoapp.api.dto.WorkoutSummaryDTO;
import com.treinoapp.api.exception.ExerciseNotFoundException;
import com.treinoapp.api.exception.MemberNotFoundException;
import com.treinoapp.api.exception.WorkoutExerciseNotFoundException;
import com.treinoapp.api.exception.WorkoutNotFoundException;
import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.model.Workout;
import com.treinoapp.api.model.WorkoutExercise;
import com.treinoapp.api.model.WorkoutSet;
import com.treinoapp.api.repository.ExerciseRepository;
import com.treinoapp.api.repository.MemberRepository;
import com.treinoapp.api.repository.WorkoutRepository;
import com.treinoapp.api.technique.TrainingTechniqueFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final MemberRepository memberRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository,
                          MemberRepository memberRepository,
                          ExerciseRepository exerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.memberRepository = memberRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public Workout create(String name, UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Workout workout = new Workout();
        workout.setName(name);
        workout.setMember(member);

        return workoutRepository.save(workout);
    }

    public Workout addExercise(UUID workoutId, UUID exerciseId, String technique, String notes) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));
        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ExerciseNotFoundException(exerciseId));

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setTechnique(technique != null ? technique : "NO_TECHNIQUE");
        workoutExercise.setNotes(notes);

        workout.getExercises().add(workoutExercise);

        return workoutRepository.save(workout);
    }

    public Workout addSet(UUID workoutId, UUID workoutExerciseId, Integer reps, Double weightKg) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));

        WorkoutExercise workoutExercise = workout.getExercises().stream()
                .filter(we -> we.getId().equals(workoutExerciseId))
                .findFirst()
                .orElseThrow(() -> new WorkoutExerciseNotFoundException(workoutExerciseId));

        WorkoutSet set = new WorkoutSet();
        set.setWorkoutExercise(workoutExercise);
        set.setSetNumber(workoutExercise.getSets().size() + 1);
        set.setReps(reps);
        set.setWeightKg(weightKg);

        workoutExercise.getSets().add(set);

        return workoutRepository.save(workout);
    }

    public WorkoutResponseDTO findByIdFormatted(UUID id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new WorkoutNotFoundException(id));

        List<WorkoutExerciseDTO> exerciseDTOs = workout.getExercises().stream()
                .map(we -> new WorkoutExerciseDTO(
                        we.getExercise().getName(),
                        we.getNotes(),
                        we.getTechnique(),
                        TrainingTechniqueFactory.fromCode(we.getTechnique()).getDisplayName(),
                        we.getSets().stream()
                                .map(s -> new WorkoutSetDTO(s.getSetNumber(), s.getReps(), s.getWeightKg()))
                                .toList()
                ))
                .toList();

        return new WorkoutResponseDTO(workout.getName(), workout.getMember().getName(), exerciseDTOs);
    }

    public List<WorkoutSummaryDTO> findAll(UUID memberId) {
        List<Workout> workouts = memberId != null
                ? workoutRepository.findByMemberIdOrderByCreatedAtDesc(memberId)
                : workoutRepository.findAllByOrderByCreatedAtDesc();

        return workouts.stream()
                .map(w -> new WorkoutSummaryDTO(
                        w.getId(),
                        w.getName(),
                        w.getMember().getName(),
                        w.getCreatedAt(),
                        w.getExercises().size()
                ))
                .toList();
    }
}