CREATE TABLE goal (
                      id UUID PRIMARY KEY,
                      member_id UUID NOT NULL REFERENCES member(id),
                      exercise_id UUID NOT NULL REFERENCES exercise(id),
                      target_weight_kg DOUBLE PRECISION NOT NULL,
                      target_reps INTEGER NOT NULL,
                      target_date DATE,
                      created_at TIMESTAMP NOT NULL DEFAULT now()
);