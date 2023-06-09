package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
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
}