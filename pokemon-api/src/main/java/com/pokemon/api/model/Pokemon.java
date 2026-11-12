package com.pokemon.api.model;

import jakarta.persistence.*;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "pokemons")
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "pokedex_number", nullable = false)
    private Integer pokedexNumber;

    @Column(length = 100)
    private String form;

    private Integer generation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "primary_type_id", nullable = false)
    private PokemonType primaryType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "secondary_type_id")
    private PokemonType secondaryType;

    private Integer hp;
    private Integer attack;
    private Integer defense;

    @Column(name = "special_attack")
    private Integer specialAttack;

    @Column(name = "special_defense")
    private Integer specialDefense;

    private Integer speed;

    private Double height; // en metros
    private Double weight; // en kilogramos

    @Column(length = 1000)
    private String description;

    @Column(name = "sprite_url")
    private String spriteUrl;

    public Pokemon() {
    }

    public Pokemon(Long id, String name, Integer pokedexNumber, String form, Integer generation,
                   PokemonType primaryType, PokemonType secondaryType, Integer hp,
                   Integer attack, Integer defense, Integer specialAttack,
                   Integer specialDefense, Integer speed, Double height,
                   Double weight, String description, String spriteUrl) {
        this.id = id;
        this.name = name;
        this.pokedexNumber = pokedexNumber;
        this.form = form;
        this.generation = generation;
        this.primaryType = primaryType;
        this.secondaryType = secondaryType;
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

    public PokemonType getPrimaryType() {
        return primaryType;
    }

    public void setPrimaryType(PokemonType primaryType) {
        this.primaryType = primaryType;
    }

    public PokemonType getSecondaryType() {
        return secondaryType;
    }

    public void setSecondaryType(PokemonType secondaryType) {
        this.secondaryType = secondaryType;
    }

    public Set<PokemonType> getTypes() {
        Set<PokemonType> types = new LinkedHashSet<>();
        if (this.primaryType != null) {
            types.add(this.primaryType);
        }
        if (this.secondaryType != null) {
            types.add(this.secondaryType);
        }
        return types;
    }

    public void setTypes(Set<PokemonType> types) {
        if (types == null || types.isEmpty()) {
            this.primaryType = null;
            this.secondaryType = null;
            return;
        }
        Iterator<PokemonType> it = types.iterator();
        this.primaryType = it.next();
        this.secondaryType = it.hasNext() ? it.next() : null;
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
