package com.pokemon.catalog.config;

import com.pokemon.catalog.client.TypeCatalogClient;
import com.pokemon.catalog.dto.PokemonRequest;
import com.pokemon.catalog.dto.TypeDefinition;
import com.pokemon.catalog.model.Pokemon;
import com.pokemon.catalog.repository.PokemonRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class PokemonCatalogInitializer {

    @Bean
    ApplicationRunner seedPokemons(PokemonRepository repository, TypeCatalogClient typeClient) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            Map<String, String> typeCodes = new HashMap<>();
            List<TypeDefinition> types;
            try {
                types = typeClient.findAll();
            } catch (RestClientException exception) {
                throw new IllegalStateException(
                        "Type Catalog must be running before Pokémon Catalog can seed its CSV data.", exception);
            }
            for (TypeDefinition type : types) {
                typeCodes.put(type.name().toUpperCase(), type.code());
                typeCodes.put(type.code().toUpperCase(), type.code());
            }
            Resource resource = new ClassPathResource("pokemon.csv");
            if (!resource.exists()) {
                resource = new ClassPathResource("Pokemons.csv");
            }
            if (!resource.exists()) {
                throw new IllegalStateException("No se encontró el archivo pokemon.csv ni Pokemons.csv en resources.");
            }
            List<Pokemon> records = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                if (line == null) {
                    return;
                }
                int lineNumber = 1;
                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    if (line.isBlank()) {
                        continue;
                    }
                    List<String> values = parseCsvLine(line);
                    if (values.size() < 13) {
                        continue;
                    }
                    try {
                        int pokedexNumber = Integer.parseInt(values.get(0));
                        String baseName = values.get(1).trim();
                        String form = values.get(2).trim();
                        String primaryCode = typeCodes.get(values.get(3).trim().toUpperCase());
                        String secondaryCode = values.get(4).isBlank()
                                ? null : typeCodes.get(values.get(4).trim().toUpperCase());
                        if (primaryCode == null) {
                            continue;
                        }
                        String displayName = form.isEmpty() ? baseName
                                : form.toLowerCase().contains(baseName.toLowerCase())
                                ? form : baseName + " (" + form + ")";
                        int generation = Integer.parseInt(values.get(12));
                        PokemonRequest request = new PokemonRequest(
                                displayName, pokedexNumber, form.isEmpty() ? null : form, generation,
                                java.util.Set.of(primaryCode), Integer.parseInt(values.get(6)),
                                Integer.parseInt(values.get(7)), Integer.parseInt(values.get(8)),
                                Integer.parseInt(values.get(9)), Integer.parseInt(values.get(10)),
                                Integer.parseInt(values.get(11)), null, null,
                                form.isEmpty()
                                        ? displayName + ", Pokémon de generación " + generation + "."
                                        : displayName + ", forma alternativa de " + baseName + " (Generación "
                                        + generation + ").",
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/"
                                        + pokedexNumber + ".png");
                        records.add(new Pokemon(request, secondaryCode == null
                                ? List.of(primaryCode) : List.of(primaryCode, secondaryCode)));
                    } catch (RuntimeException exception) {
                        throw new IllegalStateException("Error al procesar la línea " + lineNumber, exception);
                    }
                }
                repository.saveAll(records);
            }
        };
    }

    private List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder value = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char current = line.charAt(i);
            if (current == '"') {
                inQuotes = !inQuotes;
            } else if (current == ',' && !inQuotes) {
                values.add(value.toString().trim());
                value.setLength(0);
            } else {
                value.append(current);
            }
        }
        values.add(value.toString().trim());
        return values;
    }
}
