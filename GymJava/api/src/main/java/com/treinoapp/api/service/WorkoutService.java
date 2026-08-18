package com.treinoapp.api.service;

import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.model.Member;
import com.treinoapp.api.model.Workout;
import com.treinoapp.api.model.WorkoutExercise;
import com.treinoapp.api.repository.WorkoutRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

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

}