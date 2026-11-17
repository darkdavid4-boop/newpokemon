package com.pokemon.catalog.model;

import com.pokemon.catalog.dto.PokemonRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

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

    @Column(name = "primary_type_code", nullable = false, length = 50)
    private String primaryTypeCode;

    @Column(name = "secondary_type_code", length = 50)
    private String secondaryTypeCode;

    private Integer hp;
    private Integer attack;
    private Integer defense;

    @Column(name = "special_attack")
    private Integer specialAttack;

    @Column(name = "special_defense")
    private Integer specialDefense;

    private Integer speed;
    private Double height;
    private Double weight;

    @Column(length = 1000)
    private String description;

    @Column(name = "sprite_url")
    private String spriteUrl;

    protected Pokemon() {
    }

    public Pokemon(PokemonRequest request, List<String> typeCodes) {
        apply(request, typeCodes);
    }

    public void apply(PokemonRequest request, List<String> typeCodes) {
        name = request.name().trim();
        pokedexNumber = request.pokedexNumber();
        form = request.form();
        generation = request.generation() == null ? 1 : request.generation();
        primaryTypeCode = typeCodes.get(0);
        secondaryTypeCode = typeCodes.size() > 1 ? typeCodes.get(1) : null;
        hp = request.hp();
        attack = request.attack();
        defense = request.defense();
        specialAttack = request.specialAttack();
        specialDefense = request.specialDefense();
        speed = request.speed();
        height = request.height();
        weight = request.weight();
        description = request.description();
        spriteUrl = request.spriteUrl();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Integer getPokedexNumber() { return pokedexNumber; }
    public String getForm() { return form; }
    public Integer getGeneration() { return generation; }
    public String getPrimaryTypeCode() { return primaryTypeCode; }
    public String getSecondaryTypeCode() { return secondaryTypeCode; }
    public Integer getHp() { return hp; }
    public Integer getAttack() { return attack; }
    public Integer getDefense() { return defense; }
    public Integer getSpecialAttack() { return specialAttack; }
    public Integer getSpecialDefense() { return specialDefense; }
    public Integer getSpeed() { return speed; }
    public Double getHeight() { return height; }
    public Double getWeight() { return weight; }
    public String getDescription() { return description; }
    public String getSpriteUrl() { return spriteUrl; }
}
