package com.pokemon.typecatalog.service;

import com.pokemon.typecatalog.dto.TypeRequest;
import com.pokemon.typecatalog.dto.TypeResponse;
import com.pokemon.typecatalog.model.PokemonType;
import com.pokemon.typecatalog.repository.PokemonTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TypeCatalogService {

    private final PokemonTypeRepository repository;

    public TypeCatalogService(PokemonTypeRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<TypeResponse> findAll() {
        return repository.findAll(Sort.by("name").ascending()).stream()
                .map(TypeResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TypeResponse findById(Long id) {
        return repository.findById(id).map(TypeResponse::fromEntity)
                .orElseThrow(() -> new TypeNotFoundException("Tipo no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public TypeResponse findByName(String name) {
        return repository.findByNameIgnoreCase(name).map(TypeResponse::fromEntity)
                .orElseThrow(() -> new TypeNotFoundException("Tipo no encontrado con el nombre: " + name));
    }

    @Transactional(readOnly = true)
    public TypeResponse resolve(String identifier) {
        if (identifier.matches("\\d+")) {
            long id = Long.parseLong(identifier);
            return repository.findById(id)
                    .or(() -> repository.findByCodeIgnoreCase(identifier))
                    .or(() -> repository.findByNameIgnoreCase(identifier))
                    .map(TypeResponse::fromEntity)
                    .orElseThrow(() -> new TypeNotFoundException(
                            "El tipo de Pokémon '" + identifier + "' no existe en la base de datos."));
        }
        return repository.findByCodeIgnoreCase(identifier)
                .or(() -> repository.findByNameIgnoreCase(identifier))
                .map(TypeResponse::fromEntity)
                .orElseThrow(() -> new TypeNotFoundException(
                        "El tipo de Pokémon '" + identifier + "' no existe en la base de datos."));
    }

    @Transactional
    public TypeResponse create(TypeRequest request) {
        String name = PokemonType.normalize(request.name());
        if (repository.existsByNameIgnoreCase(name) || repository.findByCodeIgnoreCase(name).isPresent()) {
            throw new DuplicateTypeException("Ya existe un tipo con el nombre: " + name);
        }
        return TypeResponse.fromEntity(repository.save(new PokemonType(name, request.description())));
    }

    @Transactional
    public TypeResponse update(Long id, TypeRequest request) {
        PokemonType type = repository.findById(id)
                .orElseThrow(() -> new TypeNotFoundException("Tipo no encontrado con ID: " + id));
        String name = PokemonType.normalize(request.name());
        repository.findByNameIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateTypeException("Ya existe otro tipo con el nombre: " + name);
            }
        });
        repository.findByCodeIgnoreCase(name).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateTypeException("Ya existe otro tipo con el nombre: " + name);
            }
        });
        type.setName(name);
        type.setDescription(request.description());
        try {
            return TypeResponse.fromEntity(repository.save(type));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateTypeException("Ya existe otro tipo con el nombre: " + name);
        }
    }

    @Transactional
    public void delete(Long id) {
        PokemonType type = repository.findById(id)
                .orElseThrow(() -> new TypeNotFoundException("Tipo no encontrado con ID: " + id));
        repository.delete(type);
    }
}
