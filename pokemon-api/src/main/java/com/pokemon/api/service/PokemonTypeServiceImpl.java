package com.pokemon.api.service;

import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.dto.PokemonTypeRequestDTO;
import com.pokemon.api.dto.PokemonTypeResponseDTO;
import com.pokemon.api.exception.DuplicateResourceException;
import com.pokemon.api.exception.ResourceNotFoundException;
import com.pokemon.api.model.Pokemon;
import com.pokemon.api.model.PokemonType;
import com.pokemon.api.repository.PokemonRepository;
import com.pokemon.api.repository.PokemonTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PokemonTypeServiceImpl implements PokemonTypeService {

    private final PokemonTypeRepository pokemonTypeRepository;
    private final PokemonRepository pokemonRepository;

    public PokemonTypeServiceImpl(PokemonTypeRepository pokemonTypeRepository,
                                  PokemonRepository pokemonRepository) {
        this.pokemonTypeRepository = pokemonTypeRepository;
        this.pokemonRepository = pokemonRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PokemonTypeResponseDTO> getAllTypes() {
        return pokemonTypeRepository.findAll(Sort.by("name").ascending())
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PokemonTypeResponseDTO getTypeById(Long id) {
        PokemonType type = pokemonTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con ID: " + id));
        return mapToResponseDTO(type);
    }

    @Override
    @Transactional(readOnly = true)
    public PokemonTypeResponseDTO getTypeByName(String name) {
        PokemonType type = pokemonTypeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con el nombre: " + name));
        return mapToResponseDTO(type);
    }

    @Override
    @Transactional
    public PokemonTypeResponseDTO createType(PokemonTypeRequestDTO requestDTO) {
        String normalizedName = requestDTO.getName().trim().toUpperCase();
        if (pokemonTypeRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException("Ya existe un tipo con el nombre: " + normalizedName);
        }

        PokemonType type = new PokemonType(normalizedName, requestDTO.getDescription());
        PokemonType saved = pokemonTypeRepository.save(type);
        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional
    public PokemonTypeResponseDTO updateType(Long id, PokemonTypeRequestDTO requestDTO) {
        PokemonType type = pokemonTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con ID: " + id));

        String normalizedName = requestDTO.getName().trim().toUpperCase();
        pokemonTypeRepository.findByNameIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException("Ya existe otro tipo con el nombre: " + normalizedName);
                    }
                });

        type.setName(normalizedName);
        type.setDescription(requestDTO.getDescription());

        PokemonType updated = pokemonTypeRepository.save(type);
        return mapToResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteType(Long id) {
        PokemonType type = pokemonTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con ID: " + id));

        long assignedCount = pokemonRepository.countByTypeId(id);
        if (assignedCount > 0) {
            throw new IllegalStateException("No se puede eliminar el tipo '" + type.getName()
                    + "' porque está asignado a " + assignedCount + " Pokémon.");
        }

        pokemonTypeRepository.delete(type);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PokemonResponseDTO> getPokemonsByTypeId(Long typeId, Pageable pageable) {
        if (!pokemonTypeRepository.existsById(typeId)) {
            throw new ResourceNotFoundException("Tipo no encontrado con ID: " + typeId);
        }

        Page<Pokemon> page = pokemonRepository.findByTypeId(typeId, pageable);

        // Si la página solicitada está fuera de rango y existen elementos, devolver la última página disponible
        if (page.isEmpty() && page.getTotalElements() > 0 && pageable.getPageNumber() >= page.getTotalPages()) {
            int lastPage = page.getTotalPages() - 1;
            Pageable fallbackPageable = PageRequest.of(lastPage, pageable.getPageSize(), pageable.getSort());
            page = pokemonRepository.findByTypeId(typeId, fallbackPageable);
        }

        return page.map(PokemonResponseDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PokemonResponseDTO> getPokemonsByTypeName(String name, Pageable pageable) {
        PokemonType type = pokemonTypeRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo no encontrado con el nombre: " + name));

        Page<Pokemon> page = pokemonRepository.findByTypeName(type.getName(), pageable);

        // Si la página solicitada está fuera de rango y existen elementos, devolver la última página disponible
        if (page.isEmpty() && page.getTotalElements() > 0 && pageable.getPageNumber() >= page.getTotalPages()) {
            int lastPage = page.getTotalPages() - 1;
            Pageable fallbackPageable = PageRequest.of(lastPage, pageable.getPageSize(), pageable.getSort());
            page = pokemonRepository.findByTypeName(type.getName(), fallbackPageable);
        }

        return page.map(PokemonResponseDTO::fromEntity);
    }

    private PokemonTypeResponseDTO mapToResponseDTO(PokemonType type) {
        long primaryCount = pokemonRepository.countByPrimaryTypeId(type.getId());
        long secondaryCount = pokemonRepository.countBySecondaryTypeId(type.getId());
        int total = (int) (primaryCount + secondaryCount);

        return new PokemonTypeResponseDTO(
                type.getId(),
                type.getName(),
                type.getDescription(),
                total,
                (int) primaryCount,
                (int) secondaryCount
        );
    }
}
