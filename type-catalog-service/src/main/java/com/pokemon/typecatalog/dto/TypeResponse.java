package com.pokemon.typecatalog.dto;

import com.pokemon.typecatalog.model.PokemonType;

public record TypeResponse(Long id, String code, String name, String description) {

    public static TypeResponse fromEntity(PokemonType type) {
        return new TypeResponse(type.getId(), type.getCode(), type.getName(), type.getDescription());
    }
}
