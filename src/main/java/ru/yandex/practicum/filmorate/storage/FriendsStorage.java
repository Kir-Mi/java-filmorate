package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface FriendsStorage {
    void update(int userId, Set<Integer> friendsIds);

    void add(int userId, int friendId);

    void remove(int userId, int friendId);

    Set<Integer> getCommonFriends(int userId1, int userId2);

    Set<Integer> getFriends(int userId);
}
