package com.pokemon.api.repository;

import com.pokemon.api.model.PokemonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PokemonTypeRepository extends JpaRepository<PokemonType, Long> {

    Optional<PokemonType> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
