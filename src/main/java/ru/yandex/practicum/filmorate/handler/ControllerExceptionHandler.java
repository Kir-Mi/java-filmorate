package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.MyApplicationException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@ControllerAdvice("ru.yandex.practicum.filmorate")
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(MyApplicationException.class)
    public ResponseEntity<Object> handleConflict(MyApplicationException ex) {
        HttpStatus responseStatus = ex.getHttpStatus();
        if (responseStatus.is4xxClientError()) {
            log.warn(ex.toString());
        } else if (responseStatus.is5xxServerError()) {
            log.warn(ex.toString());
        } else {
            log.debug(ex.toString());
        }

        return ResponseEntity
                .status(responseStatus)
                .body(Map.of("errorMessage", ex.getErrorMessage()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> validationException(Exception ex) {
        String message = "Ошибка запроса: " + ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler({RuntimeException.class, ConstraintViolationException.class})
    public ResponseEntity<String> handleException(Exception ex) {
        String message = "Ошибка валидации: " + ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(message);
    }
}
