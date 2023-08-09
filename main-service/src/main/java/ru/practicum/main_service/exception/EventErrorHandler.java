package ru.practicum.main_service.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@RestControllerAdvice
public class EventErrorHandler {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Getter
    @AllArgsConstructor
    private static class ErrorResponse {
        private final String status;
        private final String reason;
        private final String message;
        private final String errors;
        private final String time;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final MethodArgumentNotValidException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                String.format("Field: %s. Error: %s", Objects.requireNonNull(exception.getFieldError()).getField(),
                        exception.getFieldError().getDefaultMessage()),
                getErrors(exception),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final RuntimeException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(DataConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForbiddenException(final DataConflictException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT.name(),
                "For the requested operation the conditions are not met.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(DateNotCorrectException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleForbiddenException(final DateNotCorrectException exception) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(FORMATTER));
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final DataNotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                exception.getMessage(),
                getErrors(exception),
                LocalDateTime.now().format(FORMATTER));
    }

    private String getErrors(Exception exception) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        exception.printStackTrace(printWriter);
        return stringWriter.toString();
    }
}
