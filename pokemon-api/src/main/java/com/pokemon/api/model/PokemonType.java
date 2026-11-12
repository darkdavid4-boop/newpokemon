package com.pokemon.api.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "types")
public class PokemonType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    public PokemonType() {
    }

    public PokemonType(Long id, String name, String description) {
        this.id = id;
        this.name = name != null ? name.toUpperCase().trim() : null;
        this.description = description;
    }

    public PokemonType(String name, String description) {
        this(null, name, description);
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
        this.name = name != null ? name.toUpperCase().trim() : null;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PokemonType that = (PokemonType) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
