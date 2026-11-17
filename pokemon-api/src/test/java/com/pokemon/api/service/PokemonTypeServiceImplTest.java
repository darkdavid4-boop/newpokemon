package com.pokemon.api.service;

import com.pokemon.api.client.PokemonCatalogClient;
import com.pokemon.api.client.TypeCatalogClient;
import com.pokemon.api.client.TypeCounts;
import com.pokemon.api.client.TypeDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PokemonTypeServiceImplTest {

    private final TypeCatalogClient typeCatalog = mock(TypeCatalogClient.class);
    private final PokemonCatalogClient pokemonCatalog = mock(PokemonCatalogClient.class);
    private final PokemonService pokemonService = mock(PokemonService.class);
    private final PokemonTypeServiceImpl service =
            new PokemonTypeServiceImpl(typeCatalog, pokemonCatalog, pokemonService);

    @Test
    void refusesToDeleteATypeStillAssignedToPokemon() {
        when(typeCatalog.findById(1L)).thenReturn(new TypeDefinition(1L, "FLYING", "FLYING", "Flying"));
        when(pokemonCatalog.counts("FLYING")).thenReturn(new TypeCounts(132, 10, 122));

        assertThrows(IllegalStateException.class, () -> service.deleteType(1L));

        verify(typeCatalog, never()).delete(1L);
    }
}
