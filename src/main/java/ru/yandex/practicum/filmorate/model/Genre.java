package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
public class Genre {
    private int id;
    private String name;

    public static Genre makeGenre(ResultSet rs) throws SQLException {
        int id = rs.getInt("GENRE_ID");
        String name = rs.getString("GENRE_NAME");

        return builder()
                .id(id)
                .name(name)
                .build();
    }
}
