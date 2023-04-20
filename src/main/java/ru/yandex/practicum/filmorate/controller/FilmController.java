package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private Set<Film> films = new HashSet<>();
    private int filmId = 1;

    @GetMapping
    public Set<Film> getFilms() {
        return films;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (films.contains(film)) {
            String message = "Такой фильм уже существует: " + film;
            log.warn(message);
            throw new ValidationException(message);
        }
        film.setId(filmId);
        films.add(film);
        log.info("Добавлен новый фильм: " + film);
        filmId++;
        return film;
    }

    @PutMapping
    public ResponseEntity<Film> addOrUpdateFilm(@Valid @RequestBody Film film) {
        if (films.removeIf(existingFilm -> existingFilm.getId() == film.getId())) {
            films.add(film);
            log.info("Фильм обновлен: " + film);
            return ResponseEntity.ok(film);
        }
        String message = "Такой фильм не существует: " + film;
        log.warn(message);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
    }

    @ExceptionHandler({ValidationException.class, RuntimeException.class, ConstraintViolationException.class})
    public ResponseEntity<String> handleException(Exception ex) {
        String message = "Ошибка валидации: " + ex.getMessage();
        log.warn(message, ex);
        return ResponseEntity.badRequest().body(message);
    }
}