package com.pokemon.api.controller;

import com.pokemon.api.dto.PokemonRequestDTO;
import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.service.PokemonService;
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
@RequestMapping("/api/v1/pokemon")
@Tag(name = "Pokémon", description = "Endpoints para la gestión completa de Pokémon en la Pokédex")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping
    @Operation(summary = "Listar Pokémon con paginación y filtros", description = "Permite obtener un listado paginado con filtros opcionales por nombre y tipo. Si se solicita una página fuera de rango, devuelve automáticamente la última página disponible.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de Pokémon obtenido exitosamente")
    })
    public ResponseEntity<Page<PokemonResponseDTO>> getAllPokemons(
            @Parameter(description = "Filtrar por nombre o coincidencia parcial") @RequestParam(required = false) String name,
            @Parameter(description = "Filtrar por nombre de tipo (ej: FIRE, WATER, ELECTRIC)") @RequestParam(required = false) String type,
            @Parameter(description = "Número de página (inicia en 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenar") @RequestParam(defaultValue = "pokedexNumber") String sortBy,
            @Parameter(description = "Dirección de orden (asc / desc)") @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PokemonResponseDTO> pokemons = pokemonService.getAllPokemons(name, type, pageable);
        return ResponseEntity.ok(pokemons);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener Pokémon por ID", description = "Devuelve los detalles de un Pokémon por su identificador único interno.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pokémon encontrado"),
            @ApiResponse(responseCode = "404", description = "Pokémon no encontrado")
    })
    public ResponseEntity<PokemonResponseDTO> getPokemonById(@PathVariable Long id) {
        return ResponseEntity.ok(pokemonService.getPokemonById(id));
    }

    @GetMapping("/pokedex/{number}")
    @Operation(summary = "Obtener Pokémon por número de Pokédex", description = "Devuelve los Pokémon (incluyendo formas alternativas) asociados a ese número nacional de Pokédex.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pokémon encontrado(s)"),
            @ApiResponse(responseCode = "404", description = "Pokémon no encontrado")
    })
    public ResponseEntity<List<PokemonResponseDTO>> getPokemonByPokedexNumber(@PathVariable Integer number) {
        return ResponseEntity.ok(pokemonService.getPokemonByPokedexNumber(number));
    }

    @GetMapping("/random")
    @Operation(summary = "Obtener un Pokémon aleatorio", description = "Selecciona un Pokémon al azar de la base de datos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pokémon seleccionado al azar"),
            @ApiResponse(responseCode = "404", description = "No hay Pokémon registrados")
    })
    public ResponseEntity<PokemonResponseDTO> getRandomPokemon() {
        return ResponseEntity.ok(pokemonService.getRandomPokemon());
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo Pokémon", description = "Registra un nuevo Pokémon validando unicidad de nombre.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pokémon creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Tipo de Pokémon no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto por duplicidad de nombre de Pokémon")
    })
    public ResponseEntity<PokemonResponseDTO> createPokemon(@Valid @RequestBody PokemonRequestDTO requestDTO) {
        PokemonResponseDTO created = pokemonService.createPokemon(requestDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un Pokémon existente", description = "Actualiza la información de un Pokémon por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pokémon actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "404", description = "Pokémon o tipo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto por duplicidad de datos con otro Pokémon")
    })
    public ResponseEntity<PokemonResponseDTO> updatePokemon(
            @PathVariable Long id,
            @Valid @RequestBody PokemonRequestDTO requestDTO) {
        PokemonResponseDTO updated = pokemonService.updatePokemon(id, requestDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un Pokémon", description = "Elimina un Pokémon de la base de datos por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Pokémon eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Pokémon no encontrado")
    })
    public ResponseEntity<Void> deletePokemon(@PathVariable Long id) {
        pokemonService.deletePokemon(id);
        return ResponseEntity.noContent().build();
    }
}
