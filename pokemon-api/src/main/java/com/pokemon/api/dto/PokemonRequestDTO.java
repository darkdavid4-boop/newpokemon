package com.pokemon.api.dto;

import jakarta.validation.constraints.*;

import java.util.Set;

public class PokemonRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotNull(message = "El número de Pokédex es obligatorio")
    @Min(value = 1, message = "El número de Pokédex debe ser mayor a 0")
    private Integer pokedexNumber;

    @Size(max = 100, message = "La forma no puede superar los 100 caracteres")
    private String form;

    @Min(value = 1, message = "La generación debe ser mayor a 0")
    private Integer generation;

    @NotEmpty(message = "Debe asignar al menos un tipo al Pokémon")
    @Size(max = 2, message = "Un Pokémon no puede tener más de 2 tipos")
    private Set<String> types;

    @NotNull(message = "El valor de HP es obligatorio")
    @Min(value = 1, message = "El HP debe ser al menos 1")
    @Max(value = 255, message = "El HP no puede superar 255")
    private Integer hp;

    @NotNull(message = "El valor de ataque es obligatorio")
    @Min(value = 1, message = "El ataque debe ser al menos 1")
    @Max(value = 255, message = "El ataque no puede superar 255")
    private Integer attack;

    @NotNull(message = "El valor de defensa es obligatorio")
    @Min(value = 1, message = "La defensa debe ser al menos 1")
    @Max(value = 255, message = "La defensa no puede superar 255")
    private Integer defense;

    @NotNull(message = "El valor de ataque especial es obligatorio")
    @Min(value = 1, message = "El ataque especial debe ser al menos 1")
    @Max(value = 255, message = "El ataque especial no puede superar 255")
    private Integer specialAttack;

    @NotNull(message = "El valor de defensa especial es obligatorio")
    @Min(value = 1, message = "La defensa especial debe ser al menos 1")
    @Max(value = 255, message = "La defensa especial no puede superar 255")
    private Integer specialDefense;

    @NotNull(message = "El valor de velocidad es obligatorio")
    @Min(value = 1, message = "La velocidad debe ser al menos 1")
    @Max(value = 255, message = "La velocidad no puede superar 255")
    private Integer speed;

    @Positive(message = "La altura debe ser un valor positivo")
    private Double height;

    @Positive(message = "El peso debe ser un valor positivo")
    private Double weight;

    @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
    private String description;

    @Pattern(regexp = "^(http|https)://.*$", message = "El spriteUrl debe ser una URL válida (http o https)")
    private String spriteUrl;

    public PokemonRequestDTO() {
    }

    public PokemonRequestDTO(String name, Integer pokedexNumber, Set<String> types,
                             Integer hp, Integer attack, Integer defense, Integer specialAttack,
                             Integer specialDefense, Integer speed, Double height,
                             Double weight, String description, String spriteUrl) {
        this(name, pokedexNumber, null, 1, types, hp, attack, defense, specialAttack, specialDefense, speed, height, weight, description, spriteUrl);
    }

    public PokemonRequestDTO(String name, Integer pokedexNumber, String form, Integer generation,
                             Set<String> types, Integer hp, Integer attack, Integer defense,
                             Integer specialAttack, Integer specialDefense, Integer speed,
                             Double height, Double weight, String description, String spriteUrl) {
        this.name = name;
        this.pokedexNumber = pokedexNumber;
        this.form = form;
        this.generation = generation != null ? generation : 1;
        this.types = types;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.height = height;
        this.weight = weight;
        this.description = description;
        this.spriteUrl = spriteUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPokedexNumber() {
        return pokedexNumber;
    }

    public void setPokedexNumber(Integer pokedexNumber) {
        this.pokedexNumber = pokedexNumber;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public Integer getGeneration() {
        return generation;
    }

    public void setGeneration(Integer generation) {
        this.generation = generation;
    }

    public Set<String> getTypes() {
        return types;
    }

    public void setTypes(Set<String> types) {
        this.types = types;
    }

    public Integer getHp() {
        return hp;
    }

    public void setHp(Integer hp) {
        this.hp = hp;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getDefense() {
        return defense;
    }

    public void setDefense(Integer defense) {
        this.defense = defense;
    }

    public Integer getSpecialAttack() {
        return specialAttack;
    }

    public void setSpecialAttack(Integer specialAttack) {
        this.specialAttack = specialAttack;
    }

    public Integer getSpecialDefense() {
        return specialDefense;
    }

    public void setSpecialDefense(Integer specialDefense) {
        this.specialDefense = specialDefense;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpriteUrl() {
        return spriteUrl;
    }

    public void setSpriteUrl(String spriteUrl) {
        this.spriteUrl = spriteUrl;
    }
}
