package com.treinoapp.api.exception;

import java.util.UUID;

public class GoalNotFoundException extends NotFoundException {
    public GoalNotFoundException(UUID id) {
        super("Goal not found with id: " + id);
    }
}