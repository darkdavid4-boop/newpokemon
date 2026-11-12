package com.pokemon.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.api.dto.PokemonRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnInitialPokemonsList() throws Exception {
        mockMvc.perform(get("/api/v1/pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1000)));
    }

    @Test
    void shouldReturnLastPageWhenRequestedPageIsOutOfBounds() throws Exception {
        // Al pedir una página muy alta (ej: page=9999 y size=10), debe devolver la última página disponible
        mockMvc.perform(get("/api/v1/pokemon?page=9999&size=10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1000)));
    }

    @Test
    void shouldReturnPokemonById() throws Exception {
        mockMvc.perform(get("/api/v1/pokemon/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bulbasaur")))
                .andExpect(jsonPath("$.pokedexNumber", is(1)))
                .andExpect(jsonPath("$.primaryType", is("GRASS")))
                .andExpect(jsonPath("$.secondaryType", is("POISON")))
                .andExpect(jsonPath("$.types", hasItems("GRASS", "POISON")));
    }

    @Test
    void shouldReturnPokemonByPokedexNumber() throws Exception {
        mockMvc.perform(get("/api/v1/pokemon/pokedex/25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].pokedexNumber", is(25)));
    }

    @Test
    void shouldFilterPokemonsByTypeIncludingSecondary() throws Exception {
        // FLYING como secundario (ej: Charizard, Gyarados, Butterfree) debe aparecer
        mockMvc.perform(get("/api/v1/pokemon?type=FLYING")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(132)))
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].types", hasItem("FLYING")));
    }

    @Test
    void shouldCreateNewPokemonSuccessfully() throws Exception {
        PokemonRequestDTO newPokemon = new PokemonRequestDTO(
                "Miraidon",
                1008,
                Set.of("ELECTRIC", "DRAGON"),
                100, 85, 100, 135, 115, 135,
                3.5, 240.0,
                "Pokémon legendario de Paldea.",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1008.png"
        );

        mockMvc.perform(post("/api/v1/pokemon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPokemon)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Miraidon")))
                .andExpect(jsonPath("$.pokedexNumber", is(1008)))
                .andExpect(jsonPath("$.primaryType", is("ELECTRIC")))
                .andExpect(jsonPath("$.secondaryType", is("DRAGON")));
    }

    @Test
    void shouldUpdatePokemonSuccessfullyWithPut() throws Exception {
        PokemonRequestDTO updateRequest = new PokemonRequestDTO(
                "Bulbasaur Mega",
                1,
                Set.of("GRASS", "POISON"),
                60, 65, 65, 80, 80, 60,
                0.8, 8.0,
                "Bulbasaur entrenado al máximo nivel con estadísticas mejoradas.",
                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"
        );

        mockMvc.perform(put("/api/v1/pokemon/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bulbasaur Mega")))
                .andExpect(jsonPath("$.hp", is(60)))
                .andExpect(jsonPath("$.attack", is(65)))
                .andExpect(jsonPath("$.primaryType", is("GRASS")))
                .andExpect(jsonPath("$.secondaryType", is("POISON")));
    }

    @Test
    void shouldReturnBadRequestWhenValidationFails() throws Exception {
        PokemonRequestDTO invalidPokemon = new PokemonRequestDTO();
        invalidPokemon.setName("");
        invalidPokemon.setPokedexNumber(-5);

        mockMvc.perform(post("/api/v1/pokemon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPokemon)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name", notNullValue()))
                .andExpect(jsonPath("$.validationErrors.pokedexNumber", notNullValue()));
    }
}
