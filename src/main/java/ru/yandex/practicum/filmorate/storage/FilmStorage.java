package ru.yandex.practicum.filmorate.storage;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Collection<Film> getFilms();

    Film addFilm(Film film);

    ResponseEntity<Film> updateFilm(Film film);

    Film getFilmById(Integer filmId);
}
