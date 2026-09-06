package com.treinoapp.api.repository;

import com.treinoapp.api.dto.ExerciseVolumeDTO;
import com.treinoapp.api.dto.SessionVolumeDTO;
import com.treinoapp.api.model.WorkoutSet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, UUID> {

    @Query("""
        SELECT SUM(s.weightKg * s.reps) FROM WorkoutSet s
        JOIN s.workoutExercise we JOIN we.workout w
        WHERE w.member.id = :memberId AND w.createdAt >= :cutoff
        """)
    Double sumVolume(@Param("memberId") UUID memberId, @Param("cutoff") Instant cutoff);

    @Query("""
        SELECT AVG(s.weightKg) FROM WorkoutSet s
        JOIN s.workoutExercise we JOIN we.workout w
        WHERE w.member.id = :memberId AND w.createdAt >= :cutoff
        """)
    Double averageWeight(@Param("memberId") UUID memberId, @Param("cutoff") Instant cutoff);

    @Query("""
        SELECT COUNT(s) FROM WorkoutSet s
        JOIN s.workoutExercise we JOIN we.workout w
        WHERE w.member.id = :memberId AND w.createdAt >= :cutoff
        """)
    Long countSets(@Param("memberId") UUID memberId, @Param("cutoff") Instant cutoff);

    @Query("""
        SELECT COUNT(DISTINCT w.id) FROM WorkoutSet s
        JOIN s.workoutExercise we JOIN we.workout w
        WHERE w.member.id = :memberId AND w.createdAt >= :cutoff
        """)
    Long countDistinctWorkouts(@Param("memberId") UUID memberId, @Param("cutoff") Instant cutoff);

    @Query("""
        SELECT new com.treinoapp.api.dto.ExerciseVolumeDTO(
            we.exercise.id, we.exercise.name, we.exercise.muscleGroup, SUM(s.weightKg * s.reps))
        FROM WorkoutSet s JOIN s.workoutExercise we JOIN we.workout w
        WHERE w.member.id = :memberId AND w.createdAt >= :cutoff
        GROUP BY we.exercise.id, we.exercise.name, we.exercise.muscleGroup
        ORDER BY SUM(s.weightKg * s.reps) DESC
        """)
    List<ExerciseVolumeDTO> topExercisesByVolume(@Param("memberId") UUID memberId,
                                                 @Param("cutoff") Instant cutoff,
                                                 Pageable pageable);

    @Query("""
        SELECT new com.treinoapp.api.dto.SessionVolumeDTO(we.exercise.id, w.createdAt, SUM(s.weightKg * s.reps))
        FROM WorkoutSet s JOIN s.workoutExercise we JOIN we.workout w
        WHERE w.member.id = :memberId AND w.createdAt >= :cutoff
        GROUP BY we.exercise.id, w.createdAt
        ORDER BY we.exercise.id, w.createdAt
        """)
    List<SessionVolumeDTO> sessionVolumesByExercise(@Param("memberId") UUID memberId,
                                                    @Param("cutoff") Instant cutoff);

    @Query(value = """
        SELECT to_char(w.created_at, 'YYYY-MM') AS month, SUM(s.weight_kg * s.reps) AS volume
        FROM workout_set s
        JOIN workout_exercise we ON we.id = s.workout_exercise_id
        JOIN workout w ON w.id = we.workout_id
        WHERE w.member_id = :memberId AND w.created_at >= :cutoff
        GROUP BY month ORDER BY month
        """, nativeQuery = true)
    List<Object[]> monthlyVolumeRaw(@Param("memberId") UUID memberId, @Param("cutoff") Instant cutoff);

    @Query(value = """
        SELECT DISTINCT ON (we.exercise_id) we.exercise_id AS exerciseId, s.weight_kg AS weightKg, s.reps AS reps
        FROM workout_set s
        JOIN workout_exercise we ON we.id = s.workout_exercise_id
        JOIN workout w ON w.id = we.workout_id
        WHERE w.member_id = :memberId AND w.created_at >= :cutoff
        ORDER BY we.exercise_id, w.created_at ASC, s.set_number ASC
        """, nativeQuery = true)
    List<Object[]> firstSetPerExercise(@Param("memberId") UUID memberId, @Param("cutoff") Instant cutoff);

    @Query(value = """
        SELECT DISTINCT ON (we.exercise_id) we.exercise_id AS exerciseId, s.weight_kg AS weightKg, s.reps AS reps
        FROM workout_set s
        JOIN workout_exercise we ON we.id = s.workout_exercise_id
        JOIN workout w ON w.id = we.workout_id
        WHERE w.member_id = :memberId AND w.created_at >= :cutoff
        ORDER BY we.exercise_id, w.created_at DESC, s.set_number DESC
        """, nativeQuery = true)
    List<Object[]> lastSetPerExercise(@Param("memberId") UUID memberId, @Param("cutoff") Instant cutoff);

    @Query("""
        SELECT s FROM WorkoutSet s JOIN s.workoutExercise we JOIN we.workout w
        WHERE we.exercise.id = :exerciseId AND w.member.id = :memberId AND w.createdAt >= :cutoff
        ORDER BY w.createdAt ASC, s.setNumber ASC
        """)
    List<WorkoutSet> findFirstSetForExercise(@Param("exerciseId") UUID exerciseId,
                                             @Param("memberId") UUID memberId,
                                             @Param("cutoff") Instant cutoff,
                                             Pageable pageable);

    @Query("""
        SELECT s FROM WorkoutSet s JOIN s.workoutExercise we JOIN we.workout w
        WHERE we.exercise.id = :exerciseId AND w.member.id = :memberId AND w.createdAt >= :cutoff
        ORDER BY w.createdAt DESC, s.setNumber DESC
        """)
    List<WorkoutSet> findLastSetForExercise(@Param("exerciseId") UUID exerciseId,
                                            @Param("memberId") UUID memberId,
                                            @Param("cutoff") Instant cutoff,
                                            Pageable pageable);

    @Query("""
        SELECT s FROM WorkoutSet s JOIN s.workoutExercise we JOIN we.workout w
        WHERE we.exercise.id = :exerciseId AND w.member.id = :memberId AND w.createdAt >= :cutoff
        ORDER BY s.weightKg DESC
        """)
    List<WorkoutSet> findBestSetForExercise(@Param("exerciseId") UUID exerciseId,
                                            @Param("memberId") UUID memberId,
                                            @Param("cutoff") Instant cutoff,
                                            Pageable pageable);

    @Query("""
        SELECT SUM(s.reps) FROM WorkoutSet s JOIN s.workoutExercise we JOIN we.workout w
        WHERE we.exercise.id = :exerciseId AND w.member.id = :memberId AND w.createdAt >= :cutoff
        """)
    Long totalRepsForExercise(@Param("exerciseId") UUID exerciseId,
                              @Param("memberId") UUID memberId,
                              @Param("cutoff") Instant cutoff);

    @Query(value = """
        SELECT w.created_at AS date,
               SUM(s.weight_kg * s.reps) AS volumeKg,
               SUM(s.reps) AS totalReps,
               (SELECT s2.weight_kg FROM workout_set s2
                  JOIN workout_exercise we2 ON we2.id = s2.workout_exercise_id
                  WHERE we2.workout_id = w.id AND we2.exercise_id = :exerciseId
                  ORDER BY s2.weight_kg DESC, s2.set_number ASC LIMIT 1) AS bestSetWeightKg,
               (SELECT s2.reps FROM workout_set s2
                  JOIN workout_exercise we2 ON we2.id = s2.workout_exercise_id
                  WHERE we2.workout_id = w.id AND we2.exercise_id = :exerciseId
                  ORDER BY s2.weight_kg DESC, s2.set_number ASC LIMIT 1) AS bestSetReps
        FROM workout w
        JOIN workout_exercise we ON we.workout_id = w.id
        JOIN workout_set s ON s.workout_exercise_id = we.id
        WHERE we.exercise_id = :exerciseId AND w.member_id = :memberId AND w.created_at >= :cutoff
        GROUP BY w.id, w.created_at
        ORDER BY w.created_at DESC
        """, nativeQuery = true)
    List<Object[]> sessionsForExerciseRaw(@Param("exerciseId") UUID exerciseId,
                                          @Param("memberId") UUID memberId,
                                          @Param("cutoff") Instant cutoff);
}