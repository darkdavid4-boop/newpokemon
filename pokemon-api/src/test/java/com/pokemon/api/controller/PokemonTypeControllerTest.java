package com.pokemon.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.api.dto.PokemonTypeRequestDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PokemonTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnAllTypesWithPrimaryAndSecondaryCounts() throws Exception {
        mockMvc.perform(get("/api/v1/types")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(18))))
                .andExpect(jsonPath("$[?(@.name == 'FLYING')].pokemonCount", hasItem(132)))
                .andExpect(jsonPath("$[?(@.name == 'FLYING')].primaryCount", hasItem(10)))
                .andExpect(jsonPath("$[?(@.name == 'FLYING')].secondaryCount", hasItem(122)));
    }

    @Test
    void shouldReturnPokemonListOfSpecificTypeByNameIncludingSecondary() throws Exception {
        // El tipo FLYING tiene 10 primarios y 122 secundarios = 132 Pokémon
        mockMvc.perform(get("/api/v1/types/name/FLYING/pokemon")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(132)))
                .andExpect(jsonPath("$.content", not(empty())));
    }

    @Test
    void shouldReturnControlledConflictErrorWhenDeletingAssignedType() throws Exception {
        // El tipo con ID 1 o FIRE/FLYING está asignado a Pokémon, debe devolver un 409 Conflict controlado
        mockMvc.perform(delete("/api/v1/types/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("No se puede eliminar el tipo")));
    }

    @Test
    void shouldCreateAndDeleteUnassignedTypeSuccessfully() throws Exception {
        PokemonTypeRequestDTO newType = new PokemonTypeRequestDTO("COSMIC", "Tipo cósmico del espacio profundo");

        // Crear tipo
        String response = mockMvc.perform(post("/api/v1/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newType)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("COSMIC")))
                .andReturn().getResponse().getContentAsString();

        Number id = com.jayway.jsonpath.JsonPath.read(response, "$.id");

        // Eliminar tipo no asignado (debe permitirlo con 204)
        mockMvc.perform(delete("/api/v1/types/" + id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
