package ru.practicum.stats_service.expeptions;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final String error;
    private String fieldName;

    public ErrorResponse(String error, String field) {
        this.error = error;
        this.fieldName = field;
    }

    public ErrorResponse(String error) {
        this.error = error;
    }
}

