package com.pokemon.api.config;

import com.pokemon.api.model.Pokemon;
import com.pokemon.api.model.PokemonType;
import com.pokemon.api.repository.PokemonRepository;
import com.pokemon.api.repository.PokemonTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner initDatabase(PokemonRepository pokemonRepository,
                                  PokemonTypeRepository typeRepository) {
        return args -> {
            log.info("Inicializando tipos de Pokémon en la base de datos...");
            List<PokemonType> initialTypes = List.of(
                    new PokemonType("NORMAL", "Tipo Normal, versátil y equilibrado."),
                    new PokemonType("FIRE", "Tipo Fuego, ataques ardientes de gran potencia."),
                    new PokemonType("WATER", "Tipo Agua, adaptable y fluido como el mar."),
                    new PokemonType("GRASS", "Tipo Planta, conectado con la naturaleza."),
                    new PokemonType("ELECTRIC", "Tipo Eléctrico, veloz y paralizante."),
                    new PokemonType("ICE", "Tipo Hielo, gélido y resistente al frío."),
                    new PokemonType("FIGHTING", "Tipo Lucha, maestro en artes marciales y combate físico."),
                    new PokemonType("POISON", "Tipo Veneno, tóxico y debilitante."),
                    new PokemonType("GROUND", "Tipo Tierra, firme y con control del suelo."),
                    new PokemonType("FLYING", "Tipo Volador, libre y veloz en los cielos."),
                    new PokemonType("PSYCHIC", "Tipo Psíquico, domina la mente y la telequinesis."),
                    new PokemonType("BUG", "Tipo Bicho, prolífico y con evoluciones tempranas."),
                    new PokemonType("ROCK", "Tipo Roca, sólido y de dura coraza."),
                    new PokemonType("GHOST", "Tipo Fantasma, etéreo y bromista."),
                    new PokemonType("DRAGON", "Tipo Dragón, místico y legendario."),
                    new PokemonType("STEEL", "Tipo Acero, defensa impenetrable."),
                    new PokemonType("FAIRY", "Tipo Hada, mágico y con luz interior."),
                    new PokemonType("DARK", "Tipo Siniestro, tácticas oscuras e impredecibles.")
            );

            Map<String, PokemonType> typeMap = new HashMap<>();
            for (PokemonType t : initialTypes) {
                PokemonType saved = typeRepository.findByNameIgnoreCase(t.getName())
                        .orElseGet(() -> typeRepository.save(t));
                typeMap.put(saved.getName().toUpperCase(), saved);
            }

            if (pokemonRepository.count() > 0) {
                log.info("La base de datos ya contiene {} Pokémon, omitiendo importación del CSV.", pokemonRepository.count());
                return;
            }

            Resource resource = new ClassPathResource("pokemon.csv");
            if (!resource.exists()) {
                resource = new ClassPathResource("Pokemons.csv");
            }

            if (!resource.exists()) {
                log.warn("No se encontró el archivo pokemon.csv ni Pokemons.csv en resources.");
                return;
            }

            log.info("Cargando todos los Pokémon desde {}...", resource.getFilename());
            List<Pokemon> pokemonsToSave = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                String headerLine = reader.readLine(); // Leer encabezado
                if (headerLine == null) {
                    log.warn("El archivo CSV de Pokémon está vacío.");
                    return;
                }

                String line;
                int lineNumber = 1;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }

                    List<String> values = parseCsvLine(line);
                    if (values.size() < 13) {
                        log.warn("Línea {} omitida por formato inválido (columnas {} < 13): {}", lineNumber, values.size(), line);
                        continue;
                    }

                    try {
                        int pokedexNumber = Integer.parseInt(values.get(0));
                        String baseName = values.get(1);
                        String form = values.get(2);
                        String type1Name = values.get(3);
                        String type2Name = values.get(4);
                        int hp = Integer.parseInt(values.get(6));
                        int attack = Integer.parseInt(values.get(7));
                        int defense = Integer.parseInt(values.get(8));
                        int specialAttack = Integer.parseInt(values.get(9));
                        int specialDefense = Integer.parseInt(values.get(10));
                        int speed = Integer.parseInt(values.get(11));
                        int generation = Integer.parseInt(values.get(12));

                        // Determinar nombre único
                        String formTrimmed = form != null ? form.trim() : "";
                        String nameTrimmed = baseName != null ? baseName.trim() : "";
                        String displayName;
                        if (formTrimmed.isEmpty()) {
                            displayName = nameTrimmed;
                        } else if (formTrimmed.toLowerCase().contains(nameTrimmed.toLowerCase())) {
                            displayName = formTrimmed;
                        } else {
                            displayName = nameTrimmed + " (" + formTrimmed + ")";
                        }

                        // Resolver tipo primario y tipo secundario
                        PokemonType primaryType = null;
                        PokemonType secondaryType = null;

                        if (!type1Name.isBlank() && typeMap.containsKey(type1Name.toUpperCase().trim())) {
                            primaryType = typeMap.get(type1Name.toUpperCase().trim());
                        }
                        if (!type2Name.isBlank() && typeMap.containsKey(type2Name.toUpperCase().trim())) {
                            secondaryType = typeMap.get(type2Name.toUpperCase().trim());
                        }

                        if (primaryType == null) {
                            log.warn("Línea {} omitida por no tener tipo primario válido: {}", lineNumber, line);
                            continue;
                        }

                        String spriteUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
                                + pokedexNumber + ".png";

                        String description = formTrimmed.isEmpty()
                                ? displayName + ", Pokémon de generación " + generation + "."
                                : displayName + ", forma alternativa de " + nameTrimmed + " (Generación " + generation + ").";

                        Pokemon pokemon = new Pokemon(
                                null,
                                displayName,
                                pokedexNumber,
                                formTrimmed.isEmpty() ? null : formTrimmed,
                                generation,
                                primaryType,
                                secondaryType,
                                hp,
                                attack,
                                defense,
                                specialAttack,
                                specialDefense,
                                speed,
                                null,
                                null,
                                description,
                                spriteUrl
                        );

                        pokemonsToSave.add(pokemon);

                    } catch (Exception e) {
                        log.error("Error al procesar la línea {}: {} ({})", lineNumber, line, e.getMessage());
                    }
                }

                pokemonRepository.saveAll(pokemonsToSave);
                log.info("¡Carga completada exitosamente! Se han guardado {} Pokémon en la base de datos.", pokemonsToSave.size());

            } catch (Exception e) {
                log.error("Error crítico al leer el archivo CSV de Pokémon", e);
            }
        };
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(sb.toString().trim());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString().trim());
        return values;
    }
}
