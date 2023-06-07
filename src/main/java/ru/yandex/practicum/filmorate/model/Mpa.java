package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

@Data
@Builder
public class Mpa {
    private int id;
    private String name;

    public static Mpa makeMpa(ResultSet rs) throws SQLException {
        int id = rs.getInt("MPA_ID");
        String name = rs.getString("MPA_NAME");

        return builder()
                .id(id)
                .name(name)
                .build();
    }
}
