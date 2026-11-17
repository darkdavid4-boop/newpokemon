package com.pokemon.catalog.repository;

import com.pokemon.catalog.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon> findByNameIgnoreCase(String name);

    List<Pokemon> findByPokedexNumber(Integer pokedexNumber);

    boolean existsByNameIgnoreCase(String name);

    Page<Pokemon> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("""
            SELECT p FROM Pokemon p
            WHERE LOWER(p.primaryTypeCode) = LOWER(:typeCode)
               OR LOWER(p.secondaryTypeCode) = LOWER(:typeCode)
            """)
    Page<Pokemon> findByTypeCode(@Param("typeCode") String typeCode, Pageable pageable);

    @Query("""
            SELECT p FROM Pokemon p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
              AND (LOWER(p.primaryTypeCode) = LOWER(:typeCode)
                   OR LOWER(p.secondaryTypeCode) = LOWER(:typeCode))
            """)
    Page<Pokemon> findByNameContainingAndTypeCode(
            @Param("name") String name, @Param("typeCode") String typeCode, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Pokemon p WHERE LOWER(p.primaryTypeCode) = LOWER(:typeCode)")
    long countPrimaryByTypeCode(@Param("typeCode") String typeCode);

    @Query("SELECT COUNT(p) FROM Pokemon p WHERE LOWER(p.secondaryTypeCode) = LOWER(:typeCode)")
    long countSecondaryByTypeCode(@Param("typeCode") String typeCode);

    @Query(value = "SELECT * FROM pokemons ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Pokemon> findRandomPokemon();
}
