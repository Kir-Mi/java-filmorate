package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        if (!isUserValid(user)) {
            throw new ValidationException("Некорректные данные пользователя.", HttpStatus.BAD_REQUEST);
        }
        userStorage.addUser(user);
        if (user.getFriends() != null) {
            friendsStorage.update(user.getId(), user.getFriends());
        }
        return user;
    }

    public User updateUser(User user) {
        if (!isUserValid(user)) {
            throw new ValidationException("Некорректные данные пользователя.", HttpStatus.BAD_REQUEST);
        }
        try {
            userStorage.updateUser(user);
            if (user.getFriends() != null) {
                friendsStorage.update(user.getId(), user.getFriends());
            }
        } catch (Exception e) {
            throw new NotFoundException(String.format("Пользователь не найден в БД USERS, id=%s.", user.getId()), HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public User getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Юзер с id = {} не найден", id);
            throw new NotFoundException("Юзер не найден", HttpStatus.NOT_FOUND);
        }
        user.setFriends(friendsStorage.getFriends(id));
        return user;
    }

    public void addAsFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            log.warn("Юзер не найден");
            throw new NotFoundException(String.format("Пользователь не найден в БД USERS, id=%s.", id), HttpStatus.NOT_FOUND);
        }
        friendsStorage.add(id, friendId);
    }

    public User removeFromFriends(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (user == null || friend == null) {
            log.warn("Юзер не найден");
            throw new NotFoundException("Юзер не найден", HttpStatus.NOT_FOUND);
        }
        friendsStorage.remove(id, friendId);
        return friend;
    }

    public Collection<User> findFriends(int id) {
        if (userStorage.getUserById(id) == null)
            throw new NotFoundException(String.format("userId=%s не найден.", id), HttpStatus.NOT_FOUND);
        List<User> friends = friendsStorage.getFriends(id).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        friends.forEach(user -> user.setFriends(friendsStorage.getFriends(user.getId())));
        return friends;
    }

    public Collection<User> findCommonFriends(int id, int otherId) {
        User friend1 = userStorage.getUserById(id);
        User friend2 = userStorage.getUserById(otherId);
        if (friend1 == null || friend2 == null) {
            throw new NotFoundException("Пользователь не найден", HttpStatus.NOT_FOUND);
        }
        List<User> friends = friendsStorage.getCommonFriends(id, otherId).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
        friends.forEach(user -> user.setFriends(friendsStorage.getFriends(user.getId())));
        return friends;
    }

    private boolean isUserValid(User u) {
        if (u == null ||
                u.getLogin() == null ||
                u.getEmail() == null ||
                u.getBirthday() == null ||
                u.getEmail().isBlank() || !u.getEmail().contains("@") ||
                u.getLogin().isBlank() || u.getLogin().contains(" ") ||
                u.getBirthday().isAfter(LocalDate.now())
        ) {
            return false;
        } else {
            if (u.getName() == null || u.getName().isBlank()) {
                u.setName(u.getLogin());
            }
            return true;
        }
    }
}
