package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmGenreStorage {

    void add(int filmId, int genreId);

    List<Integer> getGenresByFilm(int filmId);

    void update(Film film);

    void remove(int filmId, int genreId);
}
