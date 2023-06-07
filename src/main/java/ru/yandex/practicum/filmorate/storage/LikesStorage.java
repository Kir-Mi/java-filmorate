package ru.yandex.practicum.filmorate.storage;

import java.util.Set;

public interface LikesStorage {
    Set<Integer> get(int filmId);

    void add(int filmId, int userId);

    void remove(int filmId, int userId);
}
