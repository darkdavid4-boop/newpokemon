package com.pokemon.typecatalog.controller;

import com.pokemon.typecatalog.dto.TypeRequest;
import com.pokemon.typecatalog.dto.TypeResponse;
import com.pokemon.typecatalog.service.TypeCatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/v1/types")
public class TypeCatalogController {

    private final TypeCatalogService service;

    public TypeCatalogController(TypeCatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<TypeResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public TypeResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/name/{name}")
    public TypeResponse findByName(@PathVariable String name) {
        return service.findByName(name);
    }

    @GetMapping("/resolve/{identifier}")
    public TypeResponse resolve(@PathVariable String identifier) {
        return service.resolve(identifier);
    }

    @PostMapping
    public ResponseEntity<TypeResponse> create(@Valid @RequestBody TypeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @PutMapping("/{id}")
    public TypeResponse update(@PathVariable Long id, @Valid @RequestBody TypeRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
