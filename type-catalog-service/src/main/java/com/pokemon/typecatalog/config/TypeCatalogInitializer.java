package com.pokemon.typecatalog.config;

import com.pokemon.typecatalog.model.PokemonType;
import com.pokemon.typecatalog.repository.PokemonTypeRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class TypeCatalogInitializer {

    @Bean
    ApplicationRunner seedTypes(PokemonTypeRepository repository) {
        return args -> {
            List<PokemonType> types = List.of(
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
                    new PokemonType("DARK", "Tipo Siniestro, tácticas oscuras e impredecibles."));
            for (PokemonType type : types) {
                if (!repository.existsByNameIgnoreCase(type.getName())) {
                    repository.save(type);
                }
            }
        };
    }
}
