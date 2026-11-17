package com.pokemon.typecatalog.repository;

import com.pokemon.typecatalog.model.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonTypeRepository extends JpaRepository<PokemonType, Long> {

    Optional<PokemonType> findByNameIgnoreCase(String name);

    Optional<PokemonType> findByCodeIgnoreCase(String code);

    boolean existsByNameIgnoreCase(String name);
}
