package com.pokemon.api.dto;

import com.pokemon.api.model.Pokemon;

import java.util.LinkedHashSet;
import java.util.Set;

public class PokemonResponseDTO {

    private Long id;
    private String name;
    private Integer pokedexNumber;
    private String form;
    private Integer generation;
    private String primaryType;
    private String secondaryType;
    private Set<String> types;
    private Integer hp;
    private Integer attack;
    private Integer defense;
    private Integer specialAttack;
    private Integer specialDefense;
    private Integer speed;
    private Integer totalStats;
    private Double height;
    private Double weight;
    private String description;
    private String spriteUrl;

    public PokemonResponseDTO() {
    }

    public PokemonResponseDTO(Long id, String name, Integer pokedexNumber, String form, Integer generation,
                              String primaryType, String secondaryType, Set<String> types, Integer hp,
                              Integer attack, Integer defense, Integer specialAttack,
                              Integer specialDefense, Integer speed, Integer totalStats,
                              Double height, Double weight, String description, String spriteUrl) {
        this.id = id;
        this.name = name;
        this.pokedexNumber = pokedexNumber;
        this.form = form;
        this.generation = generation;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
        this.types = types;
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.specialAttack = specialAttack;
        this.specialDefense = specialDefense;
        this.speed = speed;
        this.totalStats = totalStats;
        this.height = height;
        this.weight = weight;
        this.description = description;
        this.spriteUrl = spriteUrl;
    }

    public static PokemonResponseDTO fromEntity(Pokemon pokemon) {
        if (pokemon == null) {
            return null;
        }

        int total = 0;
        if (pokemon.getHp() != null) total += pokemon.getHp();
        if (pokemon.getAttack() != null) total += pokemon.getAttack();
        if (pokemon.getDefense() != null) total += pokemon.getDefense();
        if (pokemon.getSpecialAttack() != null) total += pokemon.getSpecialAttack();
        if (pokemon.getSpecialDefense() != null) total += pokemon.getSpecialDefense();
        if (pokemon.getSpeed() != null) total += pokemon.getSpeed();

        String pType = pokemon.getPrimaryType() != null ? pokemon.getPrimaryType().getName() : null;
        String sType = pokemon.getSecondaryType() != null ? pokemon.getSecondaryType().getName() : null;

        Set<String> typeNames = new LinkedHashSet<>();
        if (pType != null) typeNames.add(pType);
        if (sType != null) typeNames.add(sType);

        return new PokemonResponseDTO(
                pokemon.getId(),
                pokemon.getName(),
                pokemon.getPokedexNumber(),
                pokemon.getForm(),
                pokemon.getGeneration(),
                pType,
                sType,
                typeNames,
                pokemon.getHp(),
                pokemon.getAttack(),
                pokemon.getDefense(),
                pokemon.getSpecialAttack(),
                pokemon.getSpecialDefense(),
                pokemon.getSpeed(),
                total,
                pokemon.getHeight(),
                pokemon.getWeight(),
                pokemon.getDescription(),
                pokemon.getSpriteUrl()
        );
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

    public String getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(String primaryType) {
        this.primaryType = primaryType;
    }

    public String getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(String secondaryType) {
        this.secondaryType = secondaryType;
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

    public Integer getTotalStats() {
        return totalStats;
    }

    public void setTotalStats(Integer totalStats) {
        this.totalStats = totalStats;
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
