package com.pokemon.typecatalog.service;

public class TypeNotFoundException extends RuntimeException {

    public TypeNotFoundException(String message) {
        super(message);
    }
}
