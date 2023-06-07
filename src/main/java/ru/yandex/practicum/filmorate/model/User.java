package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Set;

@Data
@EqualsAndHashCode(exclude = "id")
@Builder
public class User {
    private int id;
    @Email(message = "Указан некорректный Email")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения не должна быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friends;

    public void addFriend(Integer id) {
        friends.add(id);
    }

    public void removeFriend(Integer id) {
        friends.remove(id);
    }

    public Set<Integer> findFriends() {
        return friends;
    }

    public static User makeUser(ResultSet rs) throws SQLException {
        int id = rs.getInt("USER_ID");
        String login = rs.getString("LOGIN");
        String name = rs.getString("USER_NAME");
        String email = rs.getString("EMAIL");
        LocalDate birthday = rs.getDate("BIRTHDAY").toLocalDate();

        return builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();
    }
}