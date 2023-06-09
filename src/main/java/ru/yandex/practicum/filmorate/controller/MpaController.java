package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    final MpaService mpaService;

    @GetMapping("/{id}")
    public Mpa get(@PathVariable int id) {
        return mpaService.get(id);
    }

    @GetMapping
    public List<Mpa> getAll() {
        return mpaService.getAll();
    }
}
