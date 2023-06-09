package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MyApplicationException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> get(int filmId) {
        String sql = "SELECT * FROM LIKES WHERE FILM_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToLike, filmId));
    }

    @Override
    public void add(int filmId, int userId) {
        String sql = "MERGE INTO LIKES(FILM_ID, USER_ID) " +
                "VALUES (?, ?)";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) throw new MyApplicationException(
                String.format("Не удалось добавить в БД LIKES, filmID=%s, userID=%s.", filmId, userId), HttpStatus.BAD_REQUEST);
    }

    @Override
    public void remove(int filmId, int userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) throw new NotFoundException(
                String.format("Ошибка при удалении из БД LIKES, filmID=%s, userID=%s.", filmId, userId), HttpStatus.NOT_FOUND);
    }

    private Integer mapRowToLike(ResultSet resultSet, int numRow) throws SQLException {
        return resultSet.getInt("USER_ID");
    }
}
