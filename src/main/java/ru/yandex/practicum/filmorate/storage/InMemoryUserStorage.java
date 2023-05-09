package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            String message = "Такой пользователь уже существует: " + user;
            log.warn(message);
            throw new ValidationException(message, HttpStatus.BAD_REQUEST);
        }

        users.put(id, user);
        log.info("Добавлен новый пользователь: " + user);
        return user;
    }

    @Override
    public ResponseEntity<User> updateUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Пользователь обновлен: " + user);
            return ResponseEntity.ok(user);
        }

        String message = "Такой пользователь не существует: " + user;
        log.warn(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(user);
    }

    public User getUserById(Integer userId) {
        return users.get(userId);
    }

    public boolean isUserExists(Integer userId) {
        return users.containsKey(userId);
    }
}
