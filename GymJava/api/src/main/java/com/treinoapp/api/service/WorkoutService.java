package com.treinoapp.api.service;

import com.treinoapp.api.dto.WorkoutExerciseDTO;
import com.treinoapp.api.dto.WorkoutResponseDTO;
import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.model.Workout;
import com.treinoapp.api.model.WorkoutExercise;
import com.treinoapp.api.repository.WorkoutRepository;
import com.treinoapp.api.technique.TrainingTechniqueFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final MemberService memberService;
    private final ExerciseService exerciseService;

    public WorkoutService(WorkoutRepository workoutRepository,
                          MemberService memberService,
                          ExerciseService exerciseService) {
        this.workoutRepository = workoutRepository;
        this.memberService = memberService;
        this.exerciseService = exerciseService;
    }

    public Workout create(String name, UUID memberId) {
        Member member = memberService.findById(memberId);

        Workout workout = new Workout();
        workout.setName(name);
        workout.setMember(member);
        return workoutRepository.save(workout);
    }

    public Workout addExercise(UUID workoutId, UUID exerciseId, Integer sets, Integer reps,
                               Double weightKg, String technique, String notes) {
        Workout workout = findById(workoutId);
        Exercise exercise = exerciseService.findById(exerciseId);

        WorkoutExercise workoutExercise = new WorkoutExercise();
        workoutExercise.setWorkout(workout);
        workoutExercise.setExercise(exercise);
        workoutExercise.setSets(sets);
        workoutExercise.setReps(reps);
        workoutExercise.setWeightKg(weightKg);
        workoutExercise.setTechnique(technique != null ? technique : "NO_TECHNIQUE");
        workoutExercise.setNotes(notes);

        workout.getExercises().add(workoutExercise);
        return workoutRepository.save(workout);
    }

    public Workout findById(UUID id) {
        return workoutRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Workout not found: " + id));
    }


    public WorkoutResponseDTO findByIdFormatted(UUID id) {
        Workout workout = findById(id);
        List<WorkoutExerciseDTO> exerciseDTOS = workout.getExercises().stream()
                .map(workoutExercise -> new WorkoutExerciseDTO(
                        workoutExercise.getExercise().getName(),
                        workoutExercise.getSets(),
                        workoutExercise.getReps(),
                        workoutExercise.getWeightKg(),
                        workoutExercise.getNotes(),
                        workoutExercise.getTechnique(),
                        TrainingTechniqueFactory.fromCode(workoutExercise.getTechnique()).getDisplayName()
                )).toList();
        return new WorkoutResponseDTO(
                workout.getName(),
                workout.getMember().getName(),
                exerciseDTOS
        );
    }
}