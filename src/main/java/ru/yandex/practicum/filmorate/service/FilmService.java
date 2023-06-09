package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;
    private final LikesStorage likesStorage;

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        films.forEach(film -> {
            if (film != null) {
                film.setGenres(filmGenreStorage.getGenresByFilm(film.getId()).stream()
                        .map(genreStorage::get)
                        .collect(Collectors.toList()));
                film.setLikes(likesStorage.get(film.getId()));
            }
        });
        return films;
    }

    public Film addFilm(Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Некорректные данные фильма.", HttpStatus.BAD_REQUEST);
        }
        film.setMpa(mpaStorage.get(film.getMpa().getId()));
        filmStorage.addFilm(film);
        if (film.getGenres() != null) {
            filmGenreStorage.update(film);
        }
        return film;
    }

    public Film updateFilm(Film film) {
        if (!isFilmValid(film)) {
            throw new ValidationException("Некорректные данные фильма.", HttpStatus.BAD_REQUEST);
        }
        if (filmStorage.getFilmById(film.getId()) == null) {
            throw new NotFoundException(
                    String.format("Невозможно обновить данные фильма, id=%s не найден.", film.getId()), HttpStatus.NOT_FOUND);
        }
        filmStorage.updateFilm(film);
        if (film.getGenres() != null) {
            filmGenreStorage.update(film);
        }
        genreStorage.setGenresNames(film);
        return film;
    }

    public Film getFilmById(int id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            log.warn("Фильм с id = {} не найден", id);
            throw new NotFoundException("Фильм не найден", HttpStatus.NOT_FOUND);
        }
        film.setGenres(filmGenreStorage.getGenresByFilm(id).stream()
                .map(genreStorage::get)
                .collect(Collectors.toList()));
        film.setLikes(likesStorage.get(id));
        return film;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (filmId == null || userId == null) {
            log.warn("Параметр id пуст");
            throw new ValidationException("id не должен быть пустым", HttpStatus.BAD_REQUEST);
        }
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException(String.format("filmId=%s не найден.", filmId), HttpStatus.NOT_FOUND);
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException(String.format("userId=%s не найден.", userId), HttpStatus.NOT_FOUND);
        }
        likesStorage.add(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException(String.format("filmId=%s не найден.", filmId), HttpStatus.NOT_FOUND);
        }
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException(String.format("userId=%s не найден.", userId), HttpStatus.NOT_FOUND);
        }
        likesStorage.remove(filmId, userId);
    }

    public List<Film> findPopularFilms(Integer count) {
        return filmStorage.getFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean isFilmValid(Film f) {
        return f != null &&
                f.getName() != null &&
                f.getDescription() != null &&
                f.getReleaseDate() != null &&
                f.getMpa() != null &&
                f.getMpa().getId() > 0 &&
                !f.getName().isBlank() &&
                f.getDescription().length() <= 200 &&
                !f.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)) &&
                f.getDuration() > 0;
    }
}
