package com.pokemon.api.service;

import com.pokemon.api.dto.PokemonRequestDTO;
import com.pokemon.api.dto.PokemonResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PokemonService {

    Page<PokemonResponseDTO> getAllPokemons(String name, String typeName, Pageable pageable);

    PokemonResponseDTO getPokemonById(Long id);

    List<PokemonResponseDTO> getPokemonByPokedexNumber(Integer pokedexNumber);

    PokemonResponseDTO getPokemonByName(String name);

    PokemonResponseDTO getRandomPokemon();

    PokemonResponseDTO createPokemon(PokemonRequestDTO pokemonRequestDTO);

    PokemonResponseDTO updatePokemon(Long id, PokemonRequestDTO pokemonRequestDTO);

    void deletePokemon(Long id);
}
