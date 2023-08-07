package ru.practicum.main_service.exception;

public class DateNotCorrectException extends RuntimeException {
    public DateNotCorrectException(String message) {
        super(message);
    }
}
