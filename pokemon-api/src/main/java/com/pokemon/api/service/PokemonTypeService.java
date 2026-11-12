package com.pokemon.api.service;

import com.pokemon.api.dto.PokemonResponseDTO;
import com.pokemon.api.dto.PokemonTypeRequestDTO;
import com.pokemon.api.dto.PokemonTypeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PokemonTypeService {

    List<PokemonTypeResponseDTO> getAllTypes();

    PokemonTypeResponseDTO getTypeById(Long id);

    PokemonTypeResponseDTO getTypeByName(String name);

    PokemonTypeResponseDTO createType(PokemonTypeRequestDTO requestDTO);

    PokemonTypeResponseDTO updateType(Long id, PokemonTypeRequestDTO requestDTO);

    void deleteType(Long id);

    Page<PokemonResponseDTO> getPokemonsByTypeId(Long typeId, Pageable pageable);

    Page<PokemonResponseDTO> getPokemonsByTypeName(String name, Pageable pageable);
}
