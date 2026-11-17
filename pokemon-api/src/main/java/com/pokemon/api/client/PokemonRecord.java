package com.pokemon.api.client;

public record PokemonRecord(
        Long id, String name, Integer pokedexNumber, String form, Integer generation,
        String primaryTypeCode, String secondaryTypeCode, Integer hp, Integer attack,
        Integer defense, Integer specialAttack, Integer specialDefense, Integer speed,
        Integer totalStats, Double height, Double weight, String description, String spriteUrl) {
}
