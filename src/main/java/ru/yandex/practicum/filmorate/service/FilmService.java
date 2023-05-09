package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private int filmId = 1;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addFilm(Film film) {
        film.setId(filmId);
        filmStorage.addFilm(film);
        filmId++;
        return film;
    }

    public ResponseEntity<Film> updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(@PathVariable int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND);
        }
        return film;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            log.warn("Параметр id пуст");
            throw new ValidationException("id не должен быть пустым", HttpStatus.BAD_REQUEST);
        }
        if (!userStorage.isUserExists(userId)) {
            log.warn("Юзер с id = {} не существует", userId);
            throw new NotFoundException("Такого юзера пока нет", HttpStatus.NOT_FOUND);
        }
        filmStorage.getFilmById(filmId).addLike(userId);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (film == null || user == null) {
            log.warn("Не хватает параметра id");
            throw new NotFoundException("Информация не найдена", HttpStatus.NOT_FOUND);
        }
        if (!film.hasLikeFromUser(userId)) {
            log.warn("У фильма {} не было лайка от юзера {}", filmId, userId);
            throw new NotFoundException("Лайка от этого юзера не было", HttpStatus.NOT_FOUND);
        }
        film.deleteLike(userId);
    }

    public List<Film> findPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing(Film::getFilmRate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
