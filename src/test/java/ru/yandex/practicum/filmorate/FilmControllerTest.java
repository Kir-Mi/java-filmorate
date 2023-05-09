package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FilmController.class)
class FilmControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    FilmService filmService;

    Film film = new Film();

    @BeforeEach
    public void setUp() {
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.of(2022, 1, 1));
        film.setDuration(90);
    }

    @Test
    public void getFilms_ReturnsSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void addFilm_WithValidRequest_ReturnsSuccess() throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addFilm_WithEmptyName_ReturnsBadRequest() throws Exception {
        film.setName("");
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addFilm_WithDescription201symbols_ReturnsBadRequest() throws Exception {
        film.setDescription("aksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhq");
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addFilm_WithDescription200symbols_ReturnsSuccess() throws Exception {
        film.setDescription("aksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdhaksjdhcbvhdjfhcnfjdh");
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addFilm_WithDate27121895_ReturnsBadRequest() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addFilm_WithDate28121895_ReturnsSuccess() throws Exception {
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addFilm_WithDuration1_ReturnsSuccess() throws Exception {
        film.setDuration(1);
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void addFilm_WithDuration0_ReturnsBadRequest() throws Exception {
        film.setDuration(0);
        String jsonRequest = objectMapper.writeValueAsString(film);

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
