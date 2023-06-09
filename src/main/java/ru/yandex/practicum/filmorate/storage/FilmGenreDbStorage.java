package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MyApplicationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(int filmId, int genreId) {
        String sql = "INSERT INTO FILM_GENRE(FILM_ID, GENRE_ID) " +
                "VALUES (?, ?)";
        if (jdbcTemplate.update(sql, filmId, genreId) == 0) throw new MyApplicationException(
                String.format("Ошибка при добавлении в БД FILM_GENRE, filmID=%s, genreID=%s.", filmId, genreId), HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<Integer> getGenresByFilm(int filmId) {
        String sql = "SELECT * FROM FILM_GENRE WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, filmId);
    }

    @Override
    public void update(Film film) {
        jdbcTemplate.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", film.getId());
        if (film.getGenres() != null) {
            List<Integer> uniq = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                int genreId = genre.getId();
                if (!uniq.contains(genreId)) {
                    jdbcTemplate.update("INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)",
                            film.getId(), genreId);
                    uniq.add(genreId);
                }
            }
        }
    }

    @Override
    public void remove(int filmId, int genreId) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ? AND GENRE_ID = ?";
        if (jdbcTemplate.update(sql, filmId, genreId) == 0) throw new MyApplicationException(
                String.format("Отсутствуют данные в БД FILM_GENRE, filmID=%s, genreID=%s.", filmId, genreId), HttpStatus.NOT_FOUND);
    }

    private Integer mapRowToGenre(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getInt("GENRE_ID");
    }
}
