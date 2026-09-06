ALTER TABLE member ADD COLUMN password VARCHAR(255) NOT NULL DEFAULT '';
ALTER TABLE member ADD COLUMN role VARCHAR(50) NOT NULL DEFAULT 'ALUNO';

-- Remove defaults after adding columns for existing rows
ALTER TABLE member ALTER COLUMN password DROP DEFAULT;
ALTER TABLE member ALTER COLUMN role DROP DEFAULT;
