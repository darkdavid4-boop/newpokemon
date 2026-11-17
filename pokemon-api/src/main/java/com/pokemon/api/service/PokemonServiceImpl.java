package com.pokemon.api.service;

import com.pokemon.api.client.DownstreamServiceException;
import com.pokemon.api.client.PageResponse;
import com.pokemon.api.client.PokemonCatalogClient;
import com.pokemon.api.client.PokemonRecord;
import com.pokemon.api.client.TypeCatalogClient;
import com.pokemon.api.client.TypeDefinition;
import com.pokemon.api.dto.PokemonRequestDTO;
import com.pokemon.api.dto.PokemonResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PokemonServiceImpl implements PokemonService {

    private final PokemonCatalogClient pokemonCatalog;
    private final TypeCatalogClient typeCatalog;

    public PokemonServiceImpl(PokemonCatalogClient pokemonCatalog, TypeCatalogClient typeCatalog) {
        this.pokemonCatalog = pokemonCatalog;
        this.typeCatalog = typeCatalog;
    }

    @Override
    public Page<PokemonResponseDTO> getAllPokemons(String name, String typeName, Pageable pageable) {
        String typeCode = null;
        if (typeName != null && !typeName.trim().isEmpty()) {
            try {
                typeCode = typeCatalog.findByName(typeName.trim()).code();
            } catch (DownstreamServiceException exception) {
                if (exception.getStatus().value() == HttpStatus.NOT_FOUND.value()) {
                    return Page.empty(pageable);
                }
                throw exception;
            }
        }
        PageResponse<PokemonRecord> response = pokemonCatalog.findAll(name, typeCode, pageable);
        Map<String, String> typeNames = typeNamesFor(response.content());
        List<PokemonResponseDTO> content = response.content().stream()
                .map(record -> toResponse(record, typeNames))
                .toList();
        Pageable responsePageable = PageRequest.of(response.number(), response.size(), pageable.getSort());
        return new PageImpl<>(content, responsePageable, response.totalElements());
    }

    @Override
    public PokemonResponseDTO getPokemonById(Long id) {
        PokemonRecord record = pokemonCatalog.findById(id);
        return toResponse(record, typeNamesFor(List.of(record)));
    }

    @Override
    public List<PokemonResponseDTO> getPokemonByPokedexNumber(Integer pokedexNumber) {
        List<PokemonRecord> records = pokemonCatalog.findByPokedexNumber(pokedexNumber);
        Map<String, String> typeNames = typeNamesFor(records);
        return records.stream().map(record -> toResponse(record, typeNames)).toList();
    }

    @Override
    public PokemonResponseDTO getPokemonByName(String name) {
        PokemonRecord record = pokemonCatalog.findByName(name);
        return toResponse(record, typeNamesFor(List.of(record)));
    }

    @Override
    public PokemonResponseDTO getRandomPokemon() {
        PokemonRecord record = pokemonCatalog.findRandom();
        return toResponse(record, typeNamesFor(List.of(record)));
    }

    @Override
    public PokemonResponseDTO createPokemon(PokemonRequestDTO request) {
        PokemonRecord record = pokemonCatalog.create(request);
        return toResponse(record, typeNamesFor(List.of(record)));
    }

    @Override
    public PokemonResponseDTO updatePokemon(Long id, PokemonRequestDTO request) {
        PokemonRecord record = pokemonCatalog.update(id, request);
        return toResponse(record, typeNamesFor(List.of(record)));
    }

    @Override
    public void deletePokemon(Long id) {
        pokemonCatalog.delete(id);
    }

    private Map<String, String> typeNamesFor(List<PokemonRecord> records) {
        Set<String> codes = records.stream()
                .flatMap(record -> java.util.stream.Stream.of(
                        record.primaryTypeCode(), record.secondaryTypeCode()))
                .filter(code -> code != null && !code.isBlank())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (codes.isEmpty()) {
            return Map.of();
        }
        Map<String, String> names = typeCatalog.findAll().stream()
                .collect(Collectors.toMap(TypeDefinition::code, TypeDefinition::name));
        for (String code : codes) {
            if (!names.containsKey(code)) {
                throw new IllegalStateException("Type Catalog no longer contains Pokémon type code: " + code);
            }
        }
        return names;
    }

    private PokemonResponseDTO toResponse(PokemonRecord record, Map<String, String> typeNames) {
        String primaryType = typeNames.get(record.primaryTypeCode());
        String secondaryType = record.secondaryTypeCode() == null
                ? null : typeNames.get(record.secondaryTypeCode());
        Set<String> types = new LinkedHashSet<>();
        types.add(primaryType);
        if (secondaryType != null) {
            types.add(secondaryType);
        }
        return new PokemonResponseDTO(
                record.id(), record.name(), record.pokedexNumber(), record.form(), record.generation(),
                primaryType, secondaryType, types, record.hp(), record.attack(), record.defense(),
                record.specialAttack(), record.specialDefense(), record.speed(), record.totalStats(),
                record.height(), record.weight(), record.description(), record.spriteUrl());
    }
}
