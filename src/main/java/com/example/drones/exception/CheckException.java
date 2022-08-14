package com.example.drones.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CheckException extends RestException {
    public CheckException(String message) {
        super(message);
    }
}
