package com.pokemon.api.controller;

import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.dto.PokemonTypeRequestDTO;
import com.pokemon.api.dto.PokemonTypeResponseDTO;
import com.pokemon.api.service.PokemonTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/types")
@Tag(name = "Tipos de Pokémon", description = "Endpoints para la gestión de tipos elementales y consulta de Pokémon por tipo")
public class PokemonTypeController {

    private final PokemonTypeService pokemonTypeService;

    public PokemonTypeController(PokemonTypeService pokemonTypeService) {
        this.pokemonTypeService = pokemonTypeService;
    }

    @GetMapping
    @Operation(summary = "Listar todos los tipos de Pokémon", description = "Devuelve la lista completa de tipos elementales registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tipos obtenida exitosamente")
    })
    public ResponseEntity<List<PokemonTypeResponseDTO>> getAllTypes() {
        return ResponseEntity.ok(pokemonTypeService.getAllTypes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tipo por ID", description = "Devuelve los datos de un tipo elemental según su identificador único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    })
    public ResponseEntity<PokemonTypeResponseDTO> getTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(pokemonTypeService.getTypeById(id));
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Obtener tipo por nombre", description = "Devuelve los datos de un tipo elemental según su nombre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo encontrado"),
            @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    })
    public ResponseEntity<PokemonTypeResponseDTO> getTypeByName(@PathVariable String name) {
        return ResponseEntity.ok(pokemonTypeService.getTypeByName(name));
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo tipo de Pokémon", description = "Registra un nuevo tipo elemental (ej: FUEGO, AGUA, DRAGÓN).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tipo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto por nombre de tipo duplicado")
    })
    public ResponseEntity<PokemonTypeResponseDTO> createType(@Valid @RequestBody PokemonTypeRequestDTO requestDTO) {
        PokemonTypeResponseDTO created = pokemonTypeService.createType(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un tipo existente", description = "Modifica los datos de un tipo elemental por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tipo actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tipo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto por nombre duplicado")
    })
    public ResponseEntity<PokemonTypeResponseDTO> updateType(
            @PathVariable Long id,
            @Valid @RequestBody PokemonTypeRequestDTO requestDTO) {
        PokemonTypeResponseDTO updated = pokemonTypeService.updateType(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un tipo", description = "Elimina un tipo elemental si no tiene Pokémon asociados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tipo eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "No se puede eliminar porque tiene Pokémon asociados"),
            @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    })
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        pokemonTypeService.deleteType(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pokemon")
    @Operation(summary = "Obtener lista de Pokémon de un tipo por ID", description = "Devuelve el listado paginado de Pokémon pertenecientes al tipo especificado por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de Pokémon obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    })
    public ResponseEntity<Page<PokemonResponseDTO>> getPokemonsByTypeId(
            @PathVariable Long id,
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenar") @RequestParam(defaultValue = "pokedexNumber") String sortBy,
            @Parameter(description = "Dirección de orden (asc / desc)") @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PokemonResponseDTO> pokemons = pokemonTypeService.getPokemonsByTypeId(id, pageable);
        return ResponseEntity.ok(pokemons);
    }

    @GetMapping("/name/{name}/pokemon")
    @Operation(summary = "Obtener lista de Pokémon de un tipo por Nombre", description = "Devuelve el listado paginado de Pokémon pertenecientes al tipo especificado por su nombre (ej: FIRE, WATER, etc.).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de Pokémon obtenido exitosamente"),
            @ApiResponse(responseCode = "404", description = "Tipo no encontrado")
    })
    public ResponseEntity<Page<PokemonResponseDTO>> getPokemonsByTypeName(
            @PathVariable String name,
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenar") @RequestParam(defaultValue = "pokedexNumber") String sortBy,
            @Parameter(description = "Dirección de orden (asc / desc)") @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PokemonResponseDTO> pokemons = pokemonTypeService.getPokemonsByTypeName(name, pageable);
        return ResponseEntity.ok(pokemons);
    }
}
