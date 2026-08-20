CREATE TABLE member (
                        id UUID PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE exercise (
                          id UUID PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          muscle_group VARCHAR(255) NOT NULL
);

CREATE TABLE workout (
                         id UUID PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         member_id UUID NOT NULL REFERENCES member(id)
);

CREATE TABLE workout_exercise (
                                  id UUID PRIMARY KEY,
                                  workout_id UUID NOT NULL REFERENCES workout(id),
                                  exercise_id UUID NOT NULL REFERENCES exercise(id),
                                  sets INTEGER NOT NULL,
                                  reps INTEGER NOT NULL,
                                  weight_kg DOUBLE PRECISION NOT NULL,
                                  technique VARCHAR(255) NOT NULL DEFAULT 'NO_TECHNIQUE',
                                  notes TEXT
);