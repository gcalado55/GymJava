package com.treinoapp.api.service;

import com.treinoapp.api.dto.ExerciseProgressDTO;
import com.treinoapp.api.dto.PreviousNoteDTO;
import com.treinoapp.api.dto.ProgressPointDTO;
import com.treinoapp.api.dto.SessionSummaryDTO;
import com.treinoapp.api.exception.ExerciseNotFoundException;
import com.treinoapp.api.model.Exercise;
import com.treinoapp.api.model.WorkoutSet;
import com.treinoapp.api.repository.ExerciseRepository;
import com.treinoapp.api.repository.WorkoutExerciseRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutExerciseRepository workoutExerciseRepository;
    private final ProgressCalculator progressCalculator;

    public ExerciseService(ExerciseRepository exerciseRepository,
                           WorkoutExerciseRepository workoutExerciseRepository,
                           ProgressCalculator progressCalculator) {
        this.exerciseRepository = exerciseRepository;
        this.workoutExerciseRepository = workoutExerciseRepository;
        this.progressCalculator = progressCalculator;

    }

    public Exercise create(String name, String muscleGroup) {
        Exercise exercise = new Exercise();
        exercise.setName(name);
        exercise.setMuscleGroup(muscleGroup);
        return exerciseRepository.save(exercise);
    }

    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    public Exercise findById(UUID id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new ExerciseNotFoundException(id));
    }

    public ExerciseProgressDTO progress(UUID exerciseId, UUID memberId) {
        Exercise exercise = findById(exerciseId);
        Instant cutoff = Instant.now().minus(90, ChronoUnit.DAYS);

        record TimedSet(WorkoutSet set, Instant date) {
        }

        List<TimedSet> sets = workoutExerciseRepository
                .findByExercise_IdAndWorkout_Member_Id(exerciseId, memberId).stream()
                .filter(we -> !we.getWorkout().isTemplate())
                .filter(we -> "COMPLETED".equals(we.getWorkout().getStatus()))
                .filter(we -> !we.getWorkout().getCreatedAt().isBefore(cutoff))
                .flatMap(we -> we.getSets().stream()
                        .map(s -> new TimedSet(s, we.getWorkout().getCreatedAt())))
                .sorted(Comparator.comparing(TimedSet::date))
                .toList();

        if (sets.isEmpty()) {
            return new ExerciseProgressDTO(exercise.getId(), exercise.getName(),
                    null, null, null, null, 0.0, 0.0, List.of(), List.of());
        }

        WorkoutSet latest = sets.get(sets.size() - 1).set();
        WorkoutSet best = sets.stream().map(TimedSet::set)
                .max(Comparator.comparingDouble(WorkoutSet::getWeightKg)).orElseThrow();
        double estimated1Rm = progressCalculator.calculateOneRepMax(best.getWeightKg(), best.getReps());

        double firstLoad = sets.stream().filter(t -> t.date().equals(sets.get(0).date()))
                .mapToDouble(t -> progressCalculator.calculateOneRepMax(t.set().getWeightKg(), t.set().getReps())).max().orElse(0.0);
        double lastLoad = sets.stream().filter(t -> t.date().equals(sets.get(sets.size() - 1).date()))
                .mapToDouble(t -> progressCalculator.calculateOneRepMax(t.set().getWeightKg(), t.set().getReps())).max().orElse(0.0);
        double progressPct = progressCalculator.percentChange(firstLoad, lastLoad);

        Map<Instant, List<WorkoutSet>> byDate = sets.stream()
                .collect(Collectors.groupingBy(TimedSet::date,
                        Collectors.mapping(TimedSet::set, Collectors.toList())));

        List<SessionSummaryDTO> sessions = byDate.entrySet().stream()
                .sorted(Map.Entry.<Instant, List<WorkoutSet>>comparingByKey().reversed())
                .map(e -> {
                    List<WorkoutSet> daySets = e.getValue();
                    double volume = daySets.size(); // sets
                    int reps = daySets.stream().mapToInt(WorkoutSet::getReps).sum();
                    WorkoutSet bestOfDay = daySets.stream()
                            .max(Comparator.comparingDouble(WorkoutSet::getWeightKg)).orElseThrow();
                    return new SessionSummaryDTO(e.getKey(), volume, reps, bestOfDay.getWeightKg(), bestOfDay.getReps());
                })
                .toList();

        List<ProgressPointDTO> points = byDate.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ProgressPointDTO(
                        e.getKey(),
                        e.getValue().stream()
                                .mapToDouble(s -> progressCalculator.calculateOneRepMax(s.getWeightKg(), s.getReps()))
                                .max().orElse(0.0)
                ))
                .toList();

        return new ExerciseProgressDTO(exercise.getId(), exercise.getName(),
                latest.getWeightKg(), latest.getReps(),
                best.getWeightKg(), best.getReps(),
                estimated1Rm, progressPct, points, sessions);
    }

    public Optional<PreviousNoteDTO> getPreviousNote(UUID exerciseId, UUID memberId) {
        return workoutExerciseRepository.findByExercise_IdAndWorkout_Member_Id(exerciseId, memberId).stream()
                .filter(we -> !we.getWorkout().isTemplate())
                .filter(we -> "COMPLETED".equals(we.getWorkout().getStatus()))
                .filter(we -> we.getNotes() != null && !we.getNotes().isBlank() || !we.getSets().isEmpty())
                .sorted(Comparator.comparing((com.treinoapp.api.model.WorkoutExercise we) -> we.getWorkout().getCreatedAt()).reversed())
                .findFirst()
                .map(we -> {
                    Instant date = we.getWorkout().getCreatedAt();
                    long daysElapsed = ChronoUnit.DAYS.between(date, Instant.now());
                    
                    Double bestWeight = null;
                    Integer bestReps = null;
                    int setsCount = we.getSets().size();
                    
                    if (!we.getSets().isEmpty()) {
                        com.treinoapp.api.model.WorkoutSet bestSet = we.getSets().stream()
                                .max(Comparator.comparingDouble(com.treinoapp.api.model.WorkoutSet::getWeightKg))
                                .orElseThrow();
                        bestWeight = bestSet.getWeightKg();
                        bestReps = bestSet.getReps();
                    }
                    
                    return new PreviousNoteDTO(date, daysElapsed, we.getNotes(), bestWeight, bestReps, setsCount);
                });
    }
}