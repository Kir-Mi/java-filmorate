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
import java.util.*;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Genre get(int id) {
        String sql = "SELECT * FROM GENRES WHERE GENRE_ID = ?";
        List<Genre> genresList = jdbcTemplate.query(sql, this::mapRowToGenre, id);
        if (genresList.isEmpty()) {
            throw new MyApplicationException(String.format("Ошибка при запросе данных из БД GENRES, id=%s.", id), HttpStatus.NOT_FOUND);
        } else {
            return genresList.get(0);
        }
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM GENRES";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    @Override
    public Film setGenresNames(Film film) {
        List<Genre> newGenres = new ArrayList<>();
        List<Genre> genresId = film.getGenres();
        if (genresId != null) {
            for (Genre genre : genresId) {
                if (genre != null && !newGenres.contains(get(genre.getId()))) {
                    newGenres.add(get(genre.getId()));
                }
            }
            newGenres.sort(Comparator.comparingInt(Genre::getId));
        }
        film.setGenres(newGenres);
        return film;
    }

    private Genre mapRowToGenre(ResultSet resultSet, int numRow) throws SQLException {
        return Genre.builder()
                .id(resultSet.getInt("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
