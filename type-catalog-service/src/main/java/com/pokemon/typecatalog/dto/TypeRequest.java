package com.pokemon.typecatalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TypeRequest(
        @NotBlank(message = "El nombre del tipo es obligatorio")
        @Size(min = 2, max = 50, message = "El nombre del tipo debe tener entre 2 y 50 caracteres")
        String name,
        @Size(max = 255, message = "La descripción no puede exceder los 255 caracteres")
        String description) {
}
