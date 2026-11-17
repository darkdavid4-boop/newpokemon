package com.pokemon.catalog.controller;

import com.pokemon.catalog.dto.PokemonRecord;
import com.pokemon.catalog.dto.PokemonRequest;
import com.pokemon.catalog.dto.PageResponse;
import com.pokemon.catalog.dto.TypeCounts;
import com.pokemon.catalog.service.PokemonService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/internal/v1/pokemons")
public class PokemonCatalogController {

    private final PokemonService service;

    public PokemonCatalogController(PokemonService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<PokemonRecord> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String typeCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pokedexNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Pageable pageable = PageRequest.of(page, size, sort(direction, sortBy));
        return PageResponse.from(service.findAll(name, typeCode, pageable));
    }

    @GetMapping("/{id}")
    public PokemonRecord findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/pokedex/{number}")
    public List<PokemonRecord> findByPokedexNumber(@PathVariable Integer number) {
        return service.findByPokedexNumber(number);
    }

    @GetMapping("/name/{name}")
    public PokemonRecord findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @GetMapping("/random")
    public PokemonRecord findRandom() {
        return service.findRandom();
    }

    @GetMapping("/by-type/{code}")
    public PageResponse<PokemonRecord> findByType(
            @PathVariable String code,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "pokedexNumber") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        return PageResponse.from(service.findByTypeCode(code, PageRequest.of(page, size, sort(direction, sortBy))));
    }

    @GetMapping("/type-counts/{code}")
    public TypeCounts counts(@PathVariable String code) {
        return service.counts(code);
    }

    @GetMapping("/type-counts")
    public Map<String, TypeCounts> allCounts() {
        return service.allTypeCounts();
    }

    @PostMapping
    public ResponseEntity<PokemonRecord> create(@Valid @RequestBody PokemonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public PokemonRecord update(@PathVariable Long id, @Valid @RequestBody PokemonRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private Sort sort(String direction, String sortBy) {
        return "desc".equalsIgnoreCase(direction) ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
    }
}
