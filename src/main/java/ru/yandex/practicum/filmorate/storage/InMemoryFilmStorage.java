package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        int id = film.getId();
        if (films.containsKey(id)) {
            String message = "Такой фильм уже существует: " + film.getName();
            log.warn(message);
            throw new ValidationException(message, HttpStatus.BAD_REQUEST);
        }
        films.put(id, film);
        log.info("Добавлен новый фильм: " + film);
        return film;
    }

    @Override
    public ResponseEntity<Film> updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм обновлен: " + film);
            return ResponseEntity.ok(film);
        }
        String message = "Такой фильм не существует: " + film;
        log.warn(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
    }

    public Film getFilmById(Integer filmId) {
        return films.get(filmId);
    }
}
