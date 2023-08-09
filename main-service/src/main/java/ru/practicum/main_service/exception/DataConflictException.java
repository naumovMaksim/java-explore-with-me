package ru.practicum.main_service.exception;

public class DataConflictException extends RuntimeException {
    public DataConflictException(String message) {
        super(message);
    }
}
