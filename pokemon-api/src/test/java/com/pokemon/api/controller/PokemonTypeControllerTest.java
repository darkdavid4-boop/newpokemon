package com.pokemon.api.controller;

import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.dto.PokemonTypeResponseDTO;
import com.pokemon.api.service.PokemonTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PokemonTypeController.class)
class PokemonTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PokemonTypeService pokemonTypeService;

    @Test
    void shouldReturnCountsAlongsideTypeDefinitions() throws Exception {
        when(pokemonTypeService.getAllTypes()).thenReturn(List.of(
                new PokemonTypeResponseDTO(1L, "FLYING", "Flying", 132, 10, 122)));

        mockMvc.perform(get("/api/v1/types").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("FLYING")))
                .andExpect(jsonPath("$[0].pokemonCount", is(132)))
                .andExpect(jsonPath("$[0].primaryCount", is(10)))
                .andExpect(jsonPath("$[0].secondaryCount", is(122)));
    }

    @Test
    void shouldReturnPokemonPageForTypeName() throws Exception {
        PokemonResponseDTO pokemon = new PokemonResponseDTO(6L, "Charizard", 6, null, 1,
                "FIRE", "FLYING", Set.of("FIRE", "FLYING"), 78, 84, 78,
                109, 85, 100, 534, 1.7, 90.5, "Flame Pokémon.", null);
        when(pokemonTypeService.getPokemonsByTypeName(eq("FLYING"), any()))
                .thenReturn(new PageImpl<>(List.of(pokemon), PageRequest.of(0, 10), 132));

        mockMvc.perform(get("/api/v1/types/name/FLYING/pokemon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(132)))
                .andExpect(jsonPath("$.content[0].types", hasItems("FIRE", "FLYING")));
    }

    @Test
    void shouldReturnControlledConflictWhenDeletingAssignedType() throws Exception {
        doThrow(new IllegalStateException("No se puede eliminar el tipo 'FLYING' porque está asignado a 132 Pokémon."))
                .when(pokemonTypeService).deleteType(1L);

        mockMvc.perform(delete("/api/v1/types/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.error", is("Conflict")))
                .andExpect(jsonPath("$.message", containsString("No se puede eliminar el tipo")));
    }
}
