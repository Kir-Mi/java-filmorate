package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.MyApplicationException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa get(int id) {
        String sql = "SELECT * FROM MPA WHERE MPA_ID = ?";
        List<Mpa> mpaList = jdbcTemplate.query(sql, this::mapRowToMpa, id);
        if (mpaList.isEmpty()) {
            throw new MyApplicationException(String.format("Ошибка при запросе данных из БД MPA, id=%s.", id), HttpStatus.NOT_FOUND);
        } else {
            return mpaList.get(0);
        }
    }

    @Override
    public List<Mpa> getAll() {
        String sql = "SELECT * FROM MPA ORDER BY MPA_ID ASC";
        List<Mpa> mpaList = jdbcTemplate.query(sql, this::mapRowToMpa);
        if (mpaList.isEmpty()) {
            throw new MyApplicationException("Ошибка при запросе данных из БД MPA", HttpStatus.NOT_FOUND);
        } else {
            return mpaList;
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int numRow) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }
}
