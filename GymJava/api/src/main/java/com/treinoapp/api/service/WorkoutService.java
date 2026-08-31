package com.treinoapp.api.service;

import com.treinoapp.api.dto.WorkoutExerciseDTO;
import com.treinoapp.api.dto.WorkoutResponseDTO;
import com.treinoapp.api.dto.WorkoutSetDTO;
import com.treinoapp.api.dto.WorkoutSummaryDTO;
import com.treinoapp.api.exception.ExerciseNotFoundException;
import com.treinoapp.api.exception.MemberNotFoundException;
import com.treinoapp.api.exception.WorkoutExerciseNotFoundException;
import com.treinoapp.api.exception.WorkoutNotFoundException;
import com.treinoapp.api.model.*;
import com.treinoapp.api.repository.ExerciseRepository;
import com.treinoapp.api.repository.MemberRepository;
import com.treinoapp.api.repository.WorkoutRepository;
import com.treinoapp.api.repository.WorkoutExerciseRepository;
import com.treinoapp.api.technique.TrainingTechniqueFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final MemberRepository memberRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;

    public WorkoutService(WorkoutRepository workoutRepository,
                          MemberRepository memberRepository,
                          ExerciseRepository exerciseRepository,
                          WorkoutExerciseRepository workoutExerciseRepository) {
        this.workoutRepository = workoutRepository;
        this.memberRepository = memberRepository;
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
    }

    public Workout create(String name, UUID memberId, boolean isTemplate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));

        Workout workout = new Workout();
        workout.setName(name);
        workout.setMember(member);
        workout.setTemplate(isTemplate);
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

    public Workout removeExercise(UUID workoutId, UUID workoutExerciseId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));

        WorkoutExercise workoutExercise = workout.getExercises().stream()
                .filter(we -> we.getId().equals(workoutExerciseId))
                .findFirst()
                .orElseThrow(() -> new WorkoutExerciseNotFoundException(workoutExerciseId));

        workout.getExercises().remove(workoutExercise);

        return workoutRepository.save(workout);
    }

    public void deleteWorkout(UUID workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));
        workoutRepository.delete(workout);
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

    public Workout completeWorkout(UUID workoutId) {
        Workout workout = workoutRepository.findById(workoutId)
                .orElseThrow(() -> new WorkoutNotFoundException(workoutId));
        workout.setStatus("COMPLETED");
        return workoutRepository.save(workout);
    }

    public Workout startFromTemplate(UUID templateId, UUID memberId) {
        Workout template = workoutRepository.findById(templateId)
                .orElseThrow(() -> new WorkoutNotFoundException(templateId));
                
        if (!template.isTemplate()) {
            throw new IllegalArgumentException("Cannot start session from a non-template workout.");
        }
        
        Workout session = new Workout();
        session.setName(template.getName());
        session.setMember(template.getMember());
        session.setTemplate(false);
        session.setStatus("IN_PROGRESS");
        session = workoutRepository.save(session);
        
        for (WorkoutExercise templateEx : template.getExercises()) {
            WorkoutExercise sessionEx = new WorkoutExercise();
            sessionEx.setWorkout(session);
            sessionEx.setExercise(templateEx.getExercise());
            sessionEx.setTechnique(templateEx.getTechnique());
            sessionEx.setNotes(templateEx.getNotes());
            workoutExerciseRepository.save(sessionEx);
            session.getExercises().add(sessionEx);
        }
        
        return session;
    }

    public Workout updateExerciseNote(UUID workoutExerciseId, String note) {
        WorkoutExercise workoutExercise = workoutExerciseRepository.findById(workoutExerciseId)
                .orElseThrow(() -> new WorkoutExerciseNotFoundException(workoutExerciseId));
        workoutExercise.setNotes(note);
        workoutExerciseRepository.save(workoutExercise);
        return workoutExercise.getWorkout();
    }

    public WorkoutResponseDTO findByIdFormatted(UUID id) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new WorkoutNotFoundException(id));

        Instant cutoff7 = Instant.now().minus(7, java.time.temporal.ChronoUnit.DAYS);

        List<WorkoutExerciseDTO> exerciseDTOs = workout.getExercises().stream()
                .map(we -> {
                    int weeklyVolume = workoutExerciseRepository.findByExercise_IdAndWorkout_Member_Id(
                            we.getExercise().getId(), workout.getMember().getId()
                    ).stream()
                    .filter(pastWe -> !pastWe.getWorkout().getCreatedAt().isBefore(cutoff7))
                    .mapToInt(pastWe -> pastWe.getSets().size())
                    .sum();

                    return new WorkoutExerciseDTO(
                        we.getId(),
                        we.getExercise().getName(),
                        we.getExercise().getMuscleGroup(),
                        we.getNotes(),
                        we.getTechnique(),
                        TrainingTechniqueFactory.fromCode(we.getTechnique()).getDisplayName(),
                        weeklyVolume,
                        we.getSets().stream()
                                .map(s -> new WorkoutSetDTO(s.getSetNumber(), s.getReps(), s.getWeightKg()))
                                .toList()
                    );
                })
                .toList();

        return new WorkoutResponseDTO(
                workout.getName(), 
                workout.getMember().getName(), 
                workout.getCreatedAt(),
                workout.getStatus(),
                workout.isTemplate(),
                exerciseDTOs
        );
    }

    public List<WorkoutSummaryDTO> findAll(UUID memberId, Boolean isTemplate) {
        List<Workout> workouts;
        if (memberId == null) {
            workouts = workoutRepository.findAllByOrderByCreatedAtDesc();
        } else {
            if (isTemplate != null) {
                workouts = workoutRepository.findByMemberIdAndIsTemplateOrderByCreatedAtDesc(memberId, isTemplate);
            } else {
                workouts = workoutRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
            }
        }

        return workouts.stream()
                .map(w -> new WorkoutSummaryDTO(
                        w.getId(),
                        w.getName(),
                        w.getMember().getName(),
                        w.getCreatedAt(),
                        w.getStatus(),
                        w.isTemplate(),
                        w.getExercises().size()
                ))
                .toList();
    }
}