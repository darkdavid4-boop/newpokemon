package com.pokemon.catalog.service;

import com.pokemon.catalog.client.TypeCatalogClient;
import com.pokemon.catalog.dto.PokemonRequest;
import com.pokemon.catalog.dto.TypeDefinition;
import com.pokemon.catalog.model.Pokemon;
import com.pokemon.catalog.repository.PokemonRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    private PokemonRepository repository;

    @Mock
    private TypeCatalogClient typeCatalogClient;

    @Test
    void resolvesAndPersistsTypeCodesWithoutSharedTypeEntities() {
        when(repository.existsByNameIgnoreCase("Testmon")).thenReturn(false);
        when(typeCatalogClient.resolve("GRASS"))
                .thenReturn(new TypeDefinition(1L, "GRASS", "GRASS", "Grass"));
        when(typeCatalogClient.resolve("POISON"))
                .thenReturn(new TypeDefinition(2L, "POISON", "POISON", "Poison"));
        when(repository.save(any(Pokemon.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PokemonRequest request = new PokemonRequest(
                "Testmon", 9999, null, 1, new LinkedHashSet<>(List.of("GRASS", "POISON")),
                40, 40, 40, 40, 40, 40, 1.0, 1.0, null, null);

        var saved = new PokemonService(repository, typeCatalogClient).create(request);

        ArgumentCaptor<Pokemon> pokemonCaptor = ArgumentCaptor.forClass(Pokemon.class);
        verify(repository).save(pokemonCaptor.capture());
        assertEquals("GRASS", pokemonCaptor.getValue().getPrimaryTypeCode());
        assertEquals("POISON", pokemonCaptor.getValue().getSecondaryTypeCode());
        assertEquals("GRASS", saved.primaryTypeCode());
        verify(typeCatalogClient).resolve("GRASS");
        verify(typeCatalogClient).resolve("POISON");
    }
}
