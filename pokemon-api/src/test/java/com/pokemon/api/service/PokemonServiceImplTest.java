package com.pokemon.api.service;

import com.pokemon.api.client.DownstreamServiceException;
import com.pokemon.api.client.PageResponse;
import com.pokemon.api.client.PokemonCatalogClient;
import com.pokemon.api.client.PokemonRecord;
import com.pokemon.api.client.TypeCatalogClient;
import com.pokemon.api.client.TypeDefinition;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PokemonServiceImplTest {

    private final PokemonCatalogClient pokemonCatalog = mock(PokemonCatalogClient.class);
    private final TypeCatalogClient typeCatalog = mock(TypeCatalogClient.class);
    private final PokemonServiceImpl service = new PokemonServiceImpl(pokemonCatalog, typeCatalog);

    @Test
    void filtersByResolvedTypeCodeAndReturnsCurrentTypeNames() {
        when(typeCatalog.findByName("FLYING"))
                .thenReturn(new TypeDefinition(1L, "FLYING", "FLYING", "Flying"));
        when(pokemonCatalog.findAll("char", "FLYING", PageRequest.of(0, 10)))
                .thenReturn(new PageResponse<>(List.of(record()), 0, 10, 1, 1, true, true, false));
        when(typeCatalog.findAll()).thenReturn(List.of(
                new TypeDefinition(1L, "FLYING", "FLYING", "Flying"),
                new TypeDefinition(2L, "FIRE", "FIRE", "Fire")));

        Page<com.pokemon.api.dto.PokemonResponseDTO> result =
                service.getAllPokemons("char", "FLYING", PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("FIRE", result.getContent().get(0).getPrimaryType());
        assertEquals("FLYING", result.getContent().get(0).getSecondaryType());
    }

    @Test
    void unknownTypeFilterReturnsAnEmptyPage() {
        when(typeCatalog.findByName("UNKNOWN"))
                .thenThrow(new DownstreamServiceException(HttpStatus.NOT_FOUND, "Type not found", null));

        Page<com.pokemon.api.dto.PokemonResponseDTO> result =
                service.getAllPokemons(null, "UNKNOWN", PageRequest.of(0, 10));

        assertEquals(0, result.getTotalElements());
        verifyNoInteractions(pokemonCatalog);
    }

    private PokemonRecord record() {
        return new PokemonRecord(6L, "Charizard", 6, null, 1, "FIRE", "FLYING",
                78, 84, 78, 109, 85, 100, 534, 1.7, 90.5, "Flame Pokémon.", null);
    }
}
