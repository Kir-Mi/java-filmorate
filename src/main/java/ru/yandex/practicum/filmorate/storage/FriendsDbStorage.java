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
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void update(int userId, Set<Integer> friendsIds) {
        String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        for (int friendId : friendsIds) {
            jdbcTemplate.update(sql, userId, friendId);
        }
    }

    public void add(int userId, int friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
        if (jdbcTemplate.update(sql, userId, friendId) == 0) throw new MyApplicationException(
                String.format("Ошибка при добавлении в БД FRIENDS, userID=%s, friendID=%s.", userId, friendId), HttpStatus.BAD_REQUEST);
    }

    @Override
    public void remove(int userId, int friendId) {
        String sql = "DELETE FROM FRIENDS WHERE USER_ID = ? and FRIEND_ID = ?";
        if (jdbcTemplate.update(sql, userId, friendId) == 0) throw new NotFoundException(
                String.format("Не удалось удалить из БД FRIENDS, userID=%s, friendID=%s.", userId, friendId), HttpStatus.NOT_FOUND);
    }

    @Override
    public Set<Integer> getCommonFriends(int userId1, int userId2) {
        String sql = "select distinct F1.FRIEND_ID " +
                "from FRIENDS F1 join FRIENDS F2 on F1.FRIEND_ID = F2.FRIEND_ID " +
                "where F1.USER_ID = ? and F2.USER_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToId, userId1, userId2));
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        String sql = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, this::mapRowToId, userId));
    }

    private Integer mapRowToId(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("FRIEND_ID");
    }
}
