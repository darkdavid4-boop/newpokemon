package com.pokemon.catalog.service;

import com.pokemon.catalog.client.TypeCatalogClient;
import com.pokemon.catalog.dto.PokemonRecord;
import com.pokemon.catalog.dto.PokemonRequest;
import com.pokemon.catalog.dto.TypeCounts;
import com.pokemon.catalog.model.Pokemon;
import com.pokemon.catalog.repository.PokemonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PokemonService {

    private final PokemonRepository repository;
    private final TypeCatalogClient typeCatalogClient;

    public PokemonService(PokemonRepository repository, TypeCatalogClient typeCatalogClient) {
        this.repository = repository;
        this.typeCatalogClient = typeCatalogClient;
    }

    @Transactional(readOnly = true)
    public Page<PokemonRecord> findAll(String name, String typeCode, Pageable pageable) {
        Page<Pokemon> page = query(name, typeCode, pageable);
        if (page.isEmpty() && page.getTotalElements() > 0 && pageable.getPageNumber() >= page.getTotalPages()) {
            page = query(name, typeCode,
                    PageRequest.of(page.getTotalPages() - 1, pageable.getPageSize(), pageable.getSort()));
        }
        return page.map(PokemonRecord::fromEntity);
    }

    private Page<Pokemon> query(String name, String typeCode, Pageable pageable) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasType = typeCode != null && !typeCode.trim().isEmpty();
        if (hasName && hasType) {
            return repository.findByNameContainingAndTypeCode(name.trim(), typeCode.trim(), pageable);
        }
        if (hasName) {
            return repository.findByNameContainingIgnoreCase(name.trim(), pageable);
        }
        if (hasType) {
            return repository.findByTypeCode(typeCode.trim(), pageable);
        }
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public PokemonRecord findById(Long id) {
        return repository.findById(id).map(PokemonRecord::fromEntity)
                .orElseThrow(() -> new PokemonNotFoundException("Pokémon no encontrado con el ID: " + id));
    }

    @Transactional(readOnly = true)
    public PokemonRecord findByName(String name) {
        return repository.findByNameIgnoreCase(name)
                .map(PokemonRecord::fromEntity)
                .orElseThrow(() -> new PokemonNotFoundException("Pokémon no encontrado con el nombre: " + name));
    }

    @Transactional(readOnly = true)
    public List<PokemonRecord> findByPokedexNumber(Integer number) {
        List<Pokemon> pokemons = repository.findByPokedexNumber(number);
        if (pokemons.isEmpty()) {
            throw new PokemonNotFoundException("Pokémon no encontrado con el número de Pokédex: " + number);
        }
        return pokemons.stream().map(PokemonRecord::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public PokemonRecord findRandom() {
        return repository.findRandomPokemon().map(PokemonRecord::fromEntity)
                .orElseThrow(() -> new PokemonNotFoundException("No hay Pokémon registrados en la base de datos"));
    }

    @Transactional
    public PokemonRecord create(PokemonRequest request) {
        if (repository.existsByNameIgnoreCase(request.name().trim())) {
            throw new DuplicatePokemonException("Ya existe un Pokémon con el nombre: " + request.name());
        }
        Pokemon pokemon = new Pokemon(request, resolveTypes(request.types()));
        return PokemonRecord.fromEntity(repository.save(pokemon));
    }

    @Transactional
    public PokemonRecord update(Long id, PokemonRequest request) {
        Pokemon pokemon = repository.findById(id)
                .orElseThrow(() -> new PokemonNotFoundException("Pokémon no encontrado con el ID: " + id));
        repository.findByNameIgnoreCase(request.name().trim()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicatePokemonException("Ya existe otro Pokémon con el nombre: " + request.name());
            }
        });
        pokemon.apply(request, resolveTypes(request.types()));
        return PokemonRecord.fromEntity(repository.save(pokemon));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new PokemonNotFoundException("Pokémon no encontrado con el ID: " + id);
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<PokemonRecord> findByTypeCode(String code, Pageable pageable) {
        Page<Pokemon> page = repository.findByTypeCode(code, pageable);
        if (page.isEmpty() && page.getTotalElements() > 0 && pageable.getPageNumber() >= page.getTotalPages()) {
            page = repository.findByTypeCode(code,
                    PageRequest.of(page.getTotalPages() - 1, pageable.getPageSize(), pageable.getSort()));
        }
        return page.map(PokemonRecord::fromEntity);
    }

    @Transactional(readOnly = true)
    public TypeCounts counts(String code) {
        int primary = Math.toIntExact(repository.countPrimaryByTypeCode(code));
        int secondary = Math.toIntExact(repository.countSecondaryByTypeCode(code));
        return new TypeCounts(primary + secondary, primary, secondary);
    }

    @Transactional(readOnly = true)
    public Map<String, TypeCounts> allTypeCounts() {
        Map<String, TypeCounts> counts = new HashMap<>();
        for (Pokemon pokemon : repository.findAll()) {
            TypeCounts current = counts.getOrDefault(
                    pokemon.getPrimaryTypeCode(), new TypeCounts(0, 0, 0));
            counts.put(pokemon.getPrimaryTypeCode(), new TypeCounts(
                    current.pokemonCount() + 1, current.primaryCount() + 1, current.secondaryCount()));
            if (pokemon.getSecondaryTypeCode() != null) {
                current = counts.getOrDefault(pokemon.getSecondaryTypeCode(), new TypeCounts(0, 0, 0));
                counts.put(pokemon.getSecondaryTypeCode(), new TypeCounts(
                        current.pokemonCount() + 1, current.primaryCount(), current.secondaryCount() + 1));
            }
        }
        return counts;
    }

    private List<String> resolveTypes(java.util.Set<String> identifiers) {
        if (identifiers == null || identifiers.isEmpty()) {
            throw new IllegalArgumentException("Debe asignar al menos un tipo al Pokémon.");
        }
        if (identifiers.size() > 2) {
            throw new IllegalArgumentException("Un Pokémon no puede tener más de 2 tipos.");
        }
        List<String> typeCodes = new ArrayList<>();
        for (String identifier : identifiers) {
            typeCodes.add(typeCatalogClient.resolve(identifier.trim()).code());
        }
        return typeCodes;
    }
}
