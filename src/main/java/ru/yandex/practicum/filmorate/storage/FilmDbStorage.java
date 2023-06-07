package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final LikesStorage likesStorage;

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT * " +
                "FROM FILMS JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID ";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> values = new HashMap<>();
        values.put("FILM_NAME", film.getName());
        values.put("DESCRIPTION", film.getDescription());
        values.put("RELEASE_DATE", film.getReleaseDate());
        values.put("DURATION", film.getDuration());
        values.put("MPA_ID", film.getMpa().getId());
        KeyHolder keyHolder = jdbcInsert
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FilM_ID")
                .executeAndReturnKeyHolder(values);
        film.setId(keyHolder.getKey().intValue());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE FILMS " +
                "SET FILM_NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? " +
                "WHERE FILM_ID = ?";
        if (jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),
                film.getId()) == 0)
            throw new NotFoundException(String.format("Фильм не найден в БД, id=%s.", film.getId()), HttpStatus.NOT_FOUND);
        return film;
    }

    @Override
    public Film getFilmById(Integer filmId) {
        String sql = "SELECT * " +
                "FROM FILMS " +
                "JOIN MPA ON FILMS.MPA_ID = MPA.MPA_ID " +
                "WHERE FILM_ID = ?";
        List<Film> query = jdbcTemplate.query(sql, this::mapRowToFilm, filmId);
        if (query.size() == 1) {
            return query.get(0);
        }
        throw new NotFoundException(String.format("Фильм не найден в БД FILMS, id=%s.", filmId), HttpStatus.NOT_FOUND);
    }

    private Film mapRowToFilm(ResultSet resultSet, int numRow) throws SQLException {
        return Film.builder()
                .id(resultSet.getInt("FILM_ID"))
                .name(resultSet.getString("FILM_NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .mpa(Mpa.builder()
                        .id(resultSet.getInt("MPA_ID"))
                        .name(resultSet.getString("MPA_NAME"))
                        .build())
                .genres(new ArrayList<>())
                .likes(likesStorage.get(resultSet.getInt("FILM_ID")))
                .build();
    }
}
