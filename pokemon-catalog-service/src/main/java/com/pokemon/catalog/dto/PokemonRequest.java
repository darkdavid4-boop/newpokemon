package com.pokemon.catalog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record PokemonRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
        String name,
        @NotNull(message = "El número de Pokédex es obligatorio")
        @Min(value = 1, message = "El número de Pokédex debe ser mayor a 0")
        Integer pokedexNumber,
        @Size(max = 100, message = "La forma no puede superar los 100 caracteres")
        String form,
        @Min(value = 1, message = "La generación debe ser mayor a 0")
        Integer generation,
        @NotEmpty(message = "Debe asignar al menos un tipo al Pokémon")
        @Size(max = 2, message = "Un Pokémon no puede tener más de 2 tipos")
        Set<String> types,
        @NotNull(message = "El valor de HP es obligatorio")
        @Min(value = 1, message = "El HP debe ser al menos 1")
        @Max(value = 255, message = "El HP no puede superar 255")
        Integer hp,
        @NotNull(message = "El valor de ataque es obligatorio")
        @Min(value = 1, message = "El ataque debe ser al menos 1")
        @Max(value = 255, message = "El ataque no puede superar 255")
        Integer attack,
        @NotNull(message = "El valor de defensa es obligatorio")
        @Min(value = 1, message = "La defensa debe ser al menos 1")
        @Max(value = 255, message = "La defensa no puede superar 255")
        Integer defense,
        @NotNull(message = "El valor de ataque especial es obligatorio")
        @Min(value = 1, message = "El ataque especial debe ser al menos 1")
        @Max(value = 255, message = "El ataque especial no puede superar 255")
        Integer specialAttack,
        @NotNull(message = "El valor de defensa especial es obligatorio")
        @Min(value = 1, message = "La defensa especial debe ser al menos 1")
        @Max(value = 255, message = "La defensa especial no puede superar 255")
        Integer specialDefense,
        @NotNull(message = "El valor de velocidad es obligatorio")
        @Min(value = 1, message = "La velocidad debe ser al menos 1")
        @Max(value = 255, message = "La velocidad no puede superar 255")
        Integer speed,
        @Positive(message = "La altura debe ser un valor positivo") Double height,
        @Positive(message = "El peso debe ser un valor positivo") Double weight,
        @Size(max = 1000, message = "La descripción no puede exceder los 1000 caracteres")
        String description,
        @Pattern(regexp = "^(http|https)://.*$", message = "El spriteUrl debe ser una URL válida (http o https)")
        String spriteUrl) {
}
