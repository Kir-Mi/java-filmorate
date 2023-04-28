package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends MyApplicationException {
    public ValidationException(final String errorMessage, HttpStatus httpStatus) {
        super(errorMessage, httpStatus);
    }
}
