package com.example.coworking;

public class InvalidWorkspaceException extends RuntimeException {
    public InvalidWorkspaceException(String message) {
        super(message);
    }
}
