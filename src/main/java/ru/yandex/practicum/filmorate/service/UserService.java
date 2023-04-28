package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private int userId = 1;


    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(userId);
        userStorage.addUser(user);
        userId++;
        return user;
    }

    public ResponseEntity<User> updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            log.warn("Юзер с id = {} не найден", id);
            throw new NotFoundException("Юзер не найден", HttpStatus.NOT_FOUND);
        }
        return user;
    }

    public void addAsFriend(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);
        if (user == null || user2 == null) {
            log.warn("Параметр id пуст");
            throw new NotFoundException("Поле id не найдено", HttpStatus.NOT_FOUND);
        }
        user.addFriend(friendId);
        user2.addFriend(id);
    }

    public User removeFromFriends(int id, int friendId) {
        User user = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);
        if (user == null || user2 == null) {
            log.warn("Параметр id пуст");
            throw new NotFoundException("Юзер не найден", HttpStatus.NOT_FOUND);
        }
        if (!user.getFriends().contains(friendId) || !user2.getFriends().contains(id)) {
            log.warn("Юзеры не были в друзьях");
            throw new NotFoundException("Нет в списке друзей", HttpStatus.NOT_FOUND);
        }
        user.getFriends().remove(friendId);
        user2.getFriends().remove(id);
        return user2;
    }

    public Collection<User> findFriends(int id) {
        Collection<User> friends = new ArrayList<>();
        for (Integer friendId : userStorage.getUserById(id).findFriends()) {
            if (userStorage.getUserById(friendId) != null) {
                friends.add(userStorage.getUserById(friendId));
            }
        }
        return friends;
    }

    public Collection<User> findCommonFriends(int id, int otherId) {
        Set<Integer> friends1 = userStorage.getUserById(id).findFriends();
        Set<Integer> friends2 = userStorage.getUserById(otherId).findFriends();
        Set<Integer> commonFriendsId = new HashSet<>(friends1);
        commonFriendsId.retainAll(friends2);
        return commonFriendsId.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
