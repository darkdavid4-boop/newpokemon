package com.pokemon.api.service;

import com.pokemon.api.dto.PokemonRequestDTO;
import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.exception.DuplicateResourceException;
import com.pokemon.api.exception.ResourceNotFoundException;
import com.pokemon.api.model.Pokemon;
import com.pokemon.api.model.PokemonType;
import com.pokemon.api.repository.PokemonRepository;
import com.pokemon.api.repository.PokemonTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class PokemonServiceImpl implements PokemonService {

    private final PokemonRepository pokemonRepository;
    private final PokemonTypeRepository pokemonTypeRepository;

    public PokemonServiceImpl(PokemonRepository pokemonRepository,
                              PokemonTypeRepository pokemonTypeRepository) {
        this.pokemonRepository = pokemonRepository;
        this.pokemonTypeRepository = pokemonTypeRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PokemonResponseDTO> getAllPokemons(String name, String typeName, Pageable pageable) {
        Page<Pokemon> page = executePokemonQuery(name, typeName, pageable);

        // Si la página solicitada está fuera de rango y existen elementos, devolver la última página disponible
        if (page.isEmpty() && page.getTotalElements() > 0 && pageable.getPageNumber() >= page.getTotalPages()) {
            int lastPage = page.getTotalPages() - 1;
            Pageable fallbackPageable = PageRequest.of(lastPage, pageable.getPageSize(), pageable.getSort());
            page = executePokemonQuery(name, typeName, fallbackPageable);
        }

        return page.map(PokemonResponseDTO::fromEntity);
    }

    private Page<Pokemon> executePokemonQuery(String name, String typeName, Pageable pageable) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasType = typeName != null && !typeName.trim().isEmpty();

        if (hasName && hasType) {
            return pokemonRepository.findByNameContainingIgnoreCaseAndTypeName(name.trim(), typeName.trim(), pageable);
        } else if (hasName) {
            return pokemonRepository.findByNameContainingIgnoreCase(name.trim(), pageable);
        } else if (hasType) {
            return pokemonRepository.findByTypeName(typeName.trim(), pageable);
        } else {
            return pokemonRepository.findAll(pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PokemonResponseDTO getPokemonById(Long id) {
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pokémon no encontrado con el ID: " + id));
        return PokemonResponseDTO.fromEntity(pokemon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PokemonResponseDTO> getPokemonByPokedexNumber(Integer pokedexNumber) {
        List<Pokemon> pokemons = pokemonRepository.findByPokedexNumber(pokedexNumber);
        if (pokemons.isEmpty()) {
            throw new ResourceNotFoundException("Pokémon no encontrado con el número de Pokédex: " + pokedexNumber);
        }
        return pokemons.stream().map(PokemonResponseDTO::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PokemonResponseDTO getPokemonByName(String name) {
        Pokemon pokemon = pokemonRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Pokémon no encontrado con el nombre: " + name));
        return PokemonResponseDTO.fromEntity(pokemon);
    }

    @Override
    @Transactional(readOnly = true)
    public PokemonResponseDTO getRandomPokemon() {
        Pokemon pokemon = pokemonRepository.findRandomPokemon()
                .orElseThrow(() -> new ResourceNotFoundException("No hay Pokémon registrados en la base de datos"));
        return PokemonResponseDTO.fromEntity(pokemon);
    }

    @Override
    @Transactional
    public PokemonResponseDTO createPokemon(PokemonRequestDTO dto) {
        if (pokemonRepository.existsByNameIgnoreCase(dto.getName().trim())) {
            throw new DuplicateResourceException("Ya existe un Pokémon con el nombre: " + dto.getName());
        }

        List<PokemonType> resolvedTypes = resolveTypes(dto.getTypes());

        Pokemon pokemon = new Pokemon();
        copyDtoProperties(dto, pokemon);
        pokemon.setPrimaryType(resolvedTypes.get(0));
        pokemon.setSecondaryType(resolvedTypes.size() > 1 ? resolvedTypes.get(1) : null);

        Pokemon saved = pokemonRepository.save(pokemon);
        return PokemonResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional
    public PokemonResponseDTO updatePokemon(Long id, PokemonRequestDTO dto) {
        Pokemon pokemon = pokemonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pokémon no encontrado con el ID: " + id));

        // Validar que el nombre no esté en uso por otro Pokémon
        pokemonRepository.findByNameIgnoreCase(dto.getName().trim())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Ya existe otro Pokémon con el nombre: " + dto.getName());
                    }
                });

        List<PokemonType> resolvedTypes = resolveTypes(dto.getTypes());

        copyDtoProperties(dto, pokemon);
        pokemon.setPrimaryType(resolvedTypes.get(0));
        pokemon.setSecondaryType(resolvedTypes.size() > 1 ? resolvedTypes.get(1) : null);

        Pokemon updated = pokemonRepository.save(pokemon);
        return PokemonResponseDTO.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deletePokemon(Long id) {
        if (!pokemonRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pokémon no encontrado con el ID: " + id);
        }
        pokemonRepository.deleteById(id);
    }

    private List<PokemonType> resolveTypes(Set<String> typeIdentifiers) {
        if (typeIdentifiers == null || typeIdentifiers.isEmpty()) {
            throw new IllegalArgumentException("Debe asignar al menos un tipo al Pokémon.");
        }
        if (typeIdentifiers.size() > 2) {
            throw new IllegalArgumentException("Un Pokémon no puede tener más de 2 tipos.");
        }

        List<PokemonType> resolved = new ArrayList<>();
        for (String identifier : typeIdentifiers) {
            String trimmed = identifier.trim();
            PokemonType type = null;

            // Intentar buscar por ID si es un número
            if (trimmed.matches("\\d+")) {
                Long typeId = Long.parseLong(trimmed);
                type = pokemonTypeRepository.findById(typeId).orElse(null);
            }

            // Si no se encontró por ID, buscar por nombre
            if (type == null) {
                type = pokemonTypeRepository.findByNameIgnoreCase(trimmed)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "El tipo de Pokémon '" + trimmed + "' no existe en la base de datos."
                        ));
            }

            resolved.add(type);
        }
        return resolved;
    }

    private void copyDtoProperties(PokemonRequestDTO dto, Pokemon pokemon) {
        pokemon.setName(dto.getName().trim());
        pokemon.setPokedexNumber(dto.getPokedexNumber());
        pokemon.setForm(dto.getForm());
        pokemon.setGeneration(dto.getGeneration() != null ? dto.getGeneration() : 1);
        pokemon.setHp(dto.getHp());
        pokemon.setAttack(dto.getAttack());
        pokemon.setDefense(dto.getDefense());
        pokemon.setSpecialAttack(dto.getSpecialAttack());
        pokemon.setSpecialDefense(dto.getSpecialDefense());
        pokemon.setSpeed(dto.getSpeed());
        pokemon.setHeight(dto.getHeight());
        pokemon.setWeight(dto.getWeight());
        pokemon.setDescription(dto.getDescription());
        pokemon.setSpriteUrl(dto.getSpriteUrl());
    }
}
