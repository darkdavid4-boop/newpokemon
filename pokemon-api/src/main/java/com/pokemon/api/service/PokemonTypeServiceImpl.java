package com.pokemon.api.service;

import com.pokemon.api.client.PokemonCatalogClient;
import com.pokemon.api.client.TypeCatalogClient;
import com.pokemon.api.client.TypeCounts;
import com.pokemon.api.client.TypeDefinition;
import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.dto.PokemonTypeRequestDTO;
import com.pokemon.api.dto.PokemonTypeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PokemonTypeServiceImpl implements PokemonTypeService {

    private final TypeCatalogClient typeCatalog;
    private final PokemonCatalogClient pokemonCatalog;
    private final PokemonService pokemonService;

    public PokemonTypeServiceImpl(TypeCatalogClient typeCatalog, PokemonCatalogClient pokemonCatalog,
                                  PokemonService pokemonService) {
        this.typeCatalog = typeCatalog;
        this.pokemonCatalog = pokemonCatalog;
        this.pokemonService = pokemonService;
    }

    @Override
    public List<PokemonTypeResponseDTO> getAllTypes() {
        Map<String, TypeCounts> counts = pokemonCatalog.allCounts();
        return typeCatalog.findAll().stream()
                .map(type -> response(type, counts))
                .toList();
    }

    @Override
    public PokemonTypeResponseDTO getTypeById(Long id) {
        TypeDefinition type = typeCatalog.findById(id);
        return response(type, pokemonCatalog.counts(type.code()));
    }

    @Override
    public PokemonTypeResponseDTO getTypeByName(String name) {
        TypeDefinition type = typeCatalog.findByName(name);
        return response(type, pokemonCatalog.counts(type.code()));
    }

    @Override
    public PokemonTypeResponseDTO createType(PokemonTypeRequestDTO requestDTO) {
        TypeDefinition type = typeCatalog.create(requestDTO);
        return response(type, pokemonCatalog.counts(type.code()));
    }

    @Override
    public PokemonTypeResponseDTO updateType(Long id, PokemonTypeRequestDTO requestDTO) {
        TypeDefinition type = typeCatalog.update(id, requestDTO);
        return response(type, pokemonCatalog.counts(type.code()));
    }

    @Override
    public void deleteType(Long id) {
        TypeDefinition type = typeCatalog.findById(id);
        long assignedCount = pokemonCatalog.counts(type.code()).pokemonCount();
        if (assignedCount > 0) {
            throw new IllegalStateException("No se puede eliminar el tipo '" + type.name()
                    + "' porque está asignado a " + assignedCount + " Pokémon.");
        }
        typeCatalog.delete(id);
    }

    @Override
    public Page<PokemonResponseDTO> getPokemonsByTypeId(Long typeId, Pageable pageable) {
        TypeDefinition type = typeCatalog.findById(typeId);
        return pokemonService.getAllPokemons(null, type.name(), pageable);
    }

    @Override
    public Page<PokemonResponseDTO> getPokemonsByTypeName(String name, Pageable pageable) {
        TypeDefinition type = typeCatalog.findByName(name);
        return pokemonService.getAllPokemons(null, type.name(), pageable);
    }

    private PokemonTypeResponseDTO response(TypeDefinition type, Map<String, TypeCounts> counts) {
        return response(type, counts.getOrDefault(type.code(), new TypeCounts(0, 0, 0)));
    }

    private PokemonTypeResponseDTO response(TypeDefinition type, TypeCounts counts) {
        return new PokemonTypeResponseDTO(type.id(), type.name(), type.description(),
                counts.pokemonCount(), counts.primaryCount(), counts.secondaryCount());
    }
}
