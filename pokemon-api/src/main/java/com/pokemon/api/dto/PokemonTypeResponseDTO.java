package com.pokemon.api.dto;

public class PokemonTypeResponseDTO {

    private Long id;
    private String name;
    private String description;
    private int pokemonCount;
    private int primaryCount;
    private int secondaryCount;

    public PokemonTypeResponseDTO() {
    }

    public PokemonTypeResponseDTO(Long id, String name, String description, int pokemonCount, int primaryCount, int secondaryCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.pokemonCount = pokemonCount;
        this.primaryCount = primaryCount;
        this.secondaryCount = secondaryCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPokemonCount() {
        return pokemonCount;
    }

    public void setPokemonCount(int pokemonCount) {
        this.pokemonCount = pokemonCount;
    }

    public int getPrimaryCount() {
        return primaryCount;
    }

    public void setPrimaryCount(int primaryCount) {
        this.primaryCount = primaryCount;
    }

    public int getSecondaryCount() {
        return secondaryCount;
    }

    public void setSecondaryCount(int secondaryCount) {
        this.secondaryCount = secondaryCount;
    }
}
