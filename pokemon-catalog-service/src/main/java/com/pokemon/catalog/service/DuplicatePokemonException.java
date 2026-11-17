package com.pokemon.catalog.service;

public class DuplicatePokemonException extends RuntimeException {

    public DuplicatePokemonException(String message) {
        super(message);
    }
}
