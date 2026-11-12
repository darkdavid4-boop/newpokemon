package com.pokemon.api.repository;

import com.pokemon.api.model.Pokemon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

    Optional<Pokemon> findByNameIgnoreCase(String name);

    List<Pokemon> findByPokedexNumber(Integer pokedexNumber);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByPokedexNumber(Integer pokedexNumber);

    Page<Pokemon> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Pokemon p LEFT JOIN p.secondaryType st WHERE LOWER(p.primaryType.name) = LOWER(:typeName) OR (st IS NOT NULL AND LOWER(st.name) = LOWER(:typeName))")
    Page<Pokemon> findByTypeName(@Param("typeName") String typeName, Pageable pageable);

    @Query("SELECT p FROM Pokemon p LEFT JOIN p.secondaryType st WHERE p.primaryType.id = :typeId OR (st IS NOT NULL AND st.id = :typeId)")
    Page<Pokemon> findByTypeId(@Param("typeId") Long typeId, Pageable pageable);

    @Query("SELECT p FROM Pokemon p LEFT JOIN p.secondaryType st WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND (LOWER(p.primaryType.name) = LOWER(:typeName) OR (st IS NOT NULL AND LOWER(st.name) = LOWER(:typeName)))")
    Page<Pokemon> findByNameContainingIgnoreCaseAndTypeName(@Param("name") String name, @Param("typeName") String typeName, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM pokemons WHERE primary_type_id = :typeId OR secondary_type_id = :typeId", nativeQuery = true)
    long countByTypeId(@Param("typeId") Long typeId);

    @Query(value = "SELECT COUNT(*) FROM pokemons WHERE primary_type_id = :typeId", nativeQuery = true)
    long countByPrimaryTypeId(@Param("typeId") Long typeId);

    @Query(value = "SELECT COUNT(*) FROM pokemons WHERE secondary_type_id = :typeId", nativeQuery = true)
    long countBySecondaryTypeId(@Param("typeId") Long typeId);

    @Query(value = "SELECT * FROM pokemons ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Pokemon> findRandomPokemon();
}
