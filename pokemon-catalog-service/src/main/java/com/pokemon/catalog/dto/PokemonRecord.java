package com.pokemon.catalog.dto;

import com.pokemon.catalog.model.Pokemon;

public record PokemonRecord(
        Long id, String name, Integer pokedexNumber, String form, Integer generation,
        String primaryTypeCode, String secondaryTypeCode, Integer hp, Integer attack,
        Integer defense, Integer specialAttack, Integer specialDefense, Integer speed,
        Integer totalStats, Double height, Double weight, String description, String spriteUrl) {

    public static PokemonRecord fromEntity(Pokemon pokemon) {
        int total = value(pokemon.getHp()) + value(pokemon.getAttack()) + value(pokemon.getDefense())
                + value(pokemon.getSpecialAttack()) + value(pokemon.getSpecialDefense()) + value(pokemon.getSpeed());
        return new PokemonRecord(
                pokemon.getId(), pokemon.getName(), pokemon.getPokedexNumber(), pokemon.getForm(),
                pokemon.getGeneration(), pokemon.getPrimaryTypeCode(), pokemon.getSecondaryTypeCode(),
                pokemon.getHp(), pokemon.getAttack(), pokemon.getDefense(), pokemon.getSpecialAttack(),
                pokemon.getSpecialDefense(), pokemon.getSpeed(), total, pokemon.getHeight(), pokemon.getWeight(),
                pokemon.getDescription(), pokemon.getSpriteUrl());
    }

    private static int value(Integer value) {
        return value == null ? 0 : value;
    }
}
