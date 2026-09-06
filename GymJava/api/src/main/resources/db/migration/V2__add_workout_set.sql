CREATE TABLE workout_set (
    id UUID PRIMARY KEY,
    workout_exercise_id UUID NOT NULL REFERENCES workout_exercise(id),
    set_number INT NOT NULL,
    reps INT NOT NULL,
    weight_kg DOUBLE PRECISION NOT NULL
);

ALTER TABLE workout_exercise
    DROP COLUMN sets,
    DROP COLUMN reps,
    DROP COLUMN weight_kg;