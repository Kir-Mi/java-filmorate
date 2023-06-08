package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("UserDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getUsers() {
        String sql = "SELECT * FROM USERS";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    public User addUser(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> values = new HashMap<>();
        values.put("LOGIN", user.getLogin());
        values.put("USER_NAME", user.getName());
        values.put("EMAIL", user.getEmail());
        values.put("BIRTHDAY", user.getBirthday());
        KeyHolder keyHolder = jdbcInsert
                .withTableName("USERS")
                .usingGeneratedKeyColumns("USER_ID")
                .executeAndReturnKeyHolder(values);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    public User updateUser(User user) {
        String sql = "UPDATE USERS " +
                "SET LOGIN = ?, USER_NAME = ?, EMAIL = ?, BIRTHDAY = ?" +
                "WHERE USER_ID = ?";
        if (jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId()) == 0)
            throw new NotFoundException(String.format("Пользователь не найден в БД USERS, id=%s.", user.getId()), HttpStatus.NOT_FOUND);
        return user;
    }

    public User getUserById(Integer userId) {
        String sql = "SELECT * FROM USERS WHERE USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToUser, userId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Пользователь не найден в БД USERS, id=%s.", userId), HttpStatus.NOT_FOUND);
        }
    }

    public boolean isUserExists(Integer userId) {
        return getUserById(userId) != null;
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("USER_ID"))
                .login(resultSet.getString("LOGIN"))
                .name(resultSet.getString("USER_NAME"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getObject("BIRTHDAY", LocalDate.class))
                .build();
    }
}
