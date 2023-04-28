package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getUsers();

    User addUser(User user);

    ResponseEntity<User> updateUser(User user);

    User getUserById(Integer userId);

    boolean isUserExists(Integer userId);
}
