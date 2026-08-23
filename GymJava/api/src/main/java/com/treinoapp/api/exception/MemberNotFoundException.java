package com.treinoapp.api.exception;

import java.util.UUID;

public class MemberNotFoundException extends NotFoundException{
    public MemberNotFoundException(UUID id) {
        super("Member not found" + id);
    }
}
