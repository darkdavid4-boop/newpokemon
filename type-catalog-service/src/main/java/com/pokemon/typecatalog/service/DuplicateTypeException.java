package com.pokemon.typecatalog.service;

public class DuplicateTypeException extends RuntimeException {

    public DuplicateTypeException(String message) {
        super(message);
    }
}
