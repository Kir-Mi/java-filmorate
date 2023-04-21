package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import javax.validation.ConstraintViolationException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> ValidationException(Exception ex) {
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
