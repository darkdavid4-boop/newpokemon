package com.pokemon.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.api.dto.PokemonRequestDTO;
import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.service.PokemonService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PokemonController.class)
class PokemonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PokemonService pokemonService;

    @Test
    void shouldReturnPokemonByIdWithPublicTypeNames() throws Exception {
        when(pokemonService.getPokemonById(1L)).thenReturn(bulbasaur());

        mockMvc.perform(get("/api/v1/pokemon/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Bulbasaur")))
                .andExpect(jsonPath("$.primaryType", is("GRASS")))
                .andExpect(jsonPath("$.secondaryType", is("POISON")))
                .andExpect(jsonPath("$.types", hasItems("GRASS", "POISON")));
    }

    @Test
    void shouldForwardFiltersAndReturnPagedPokemon() throws Exception {
        when(pokemonService.getAllPokemons(eq("char"), eq("FLYING"), any()))
                .thenReturn(new PageImpl<>(List.of(bulbasaur()), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/pokemon")
                        .param("name", "char")
                        .param("type", "FLYING")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(1)))
                .andExpect(jsonPath("$.content[0].types", hasItems("GRASS", "POISON")));

        verify(pokemonService).getAllPokemons(eq("char"), eq("FLYING"), any());
    }

    @Test
    void shouldReturnCreatedPokemon() throws Exception {
        PokemonRequestDTO request = new PokemonRequestDTO(
                "Miraidon", 1008, Set.of("ELECTRIC", "DRAGON"),
                100, 85, 100, 135, 115, 135, 3.5, 240.0, "Legendary Pokémon.", null);
        when(pokemonService.createPokemon(any())).thenReturn(bulbasaur());

        mockMvc.perform(post("/api/v1/pokemon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Bulbasaur")));
    }

    @Test
    void shouldRejectInvalidRequestBeforeCallingCatalog() throws Exception {
        PokemonRequestDTO request = new PokemonRequestDTO();
        request.setName("");
        request.setPokedexNumber(-5);

        mockMvc.perform(post("/api/v1/pokemon")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.name", notNullValue()))
                .andExpect(jsonPath("$.validationErrors.pokedexNumber", notNullValue()));

        verifyNoInteractions(pokemonService);
    }

    private PokemonResponseDTO bulbasaur() {
        return new PokemonResponseDTO(1L, "Bulbasaur", 1, null, 1, "GRASS", "POISON",
                Set.of("GRASS", "POISON"), 45, 49, 49, 65, 65, 45, 318,
                0.7, 6.9, "Seed Pokémon.", "https://example.test/1.png");
    }
}
