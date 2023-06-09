package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreDbStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    final GenreDbStorage genreDbStorage;

    public Genre get(int id) {
        Genre result = genreDbStorage.get(id);
        if (result == null) {
            throw new NotFoundException(String.format("genreID=%s не найден.", id), HttpStatus.NOT_FOUND);
        }
        return result;
    }

    public List<Genre> getAll() {
        return genreDbStorage.getAll();
    }
}
