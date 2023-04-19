package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private Set<User> users = new HashSet<>();
    private int userId = 1;

    @GetMapping
    public Set<User> getUsers() {
        return users;
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        if (users.contains(user)) {
            String message = "Такой пользователь уже существует: " + user;
            log.warn(message);
            throw new ValidationException(message);
        }
        if(user.getName() == null || user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        user.setId(userId);
        users.add(user);
        log.info("Добавлен новый пользователь: " + user);
        userId++;
        return user;
    }

    @PutMapping
    public ResponseEntity<User> addOrUpdateUser(@Valid @RequestBody User user) {
        if(user.getName().isBlank()){
            user.setName(user.getLogin());
        }
        if (users.removeIf(existingUser -> existingUser.getId() == user.getId())) {
            users.add(user);
            log.info("Пользователь обновлен: " + user);
            return ResponseEntity.ok(user);
        }

        String message = "Такой пользователь не существует: " + user;
        log.warn(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
    }

    @ExceptionHandler({ValidationException.class, ConstraintViolationException.class})
    public ResponseEntity<String> handleException(Exception ex) {
        String message = "Ошибка валидации: " + ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(message);
    }
}
