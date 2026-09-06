ALTER TABLE workout_exercise ADD COLUMN order_index INT NOT NULL DEFAULT 0;
ALTER TABLE workout_exercise ADD COLUMN log_notes TEXT;

UPDATE workout_exercise SET order_index = 0 WHERE order_index IS NULL;
