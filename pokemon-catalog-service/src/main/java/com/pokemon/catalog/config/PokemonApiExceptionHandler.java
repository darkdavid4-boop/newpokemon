package com.pokemon.catalog.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pokemon.catalog.service.DuplicatePokemonException;
import com.pokemon.catalog.service.PokemonNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;

@RestControllerAdvice
public class PokemonApiExceptionHandler {

    private final ObjectMapper objectMapper;

    public PokemonApiExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(PokemonNotFoundException.class)
    public ResponseEntity<ApiError> notFound(PokemonNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicatePokemonException.class)
    public ResponseEntity<ApiError> conflict(DuplicatePokemonException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), request, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> invalidArgument(IllegalArgumentException exception, HttpServletRequest request) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(field -> errors.put(field.getField(), field.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Errores de validación en los datos enviados", request, errors);
    }

    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<ApiError> remoteError(
            RestClientResponseException exception, HttpServletRequest request) {
        HttpStatusCode status = exception.getStatusCode();
        HttpStatus resolved = HttpStatus.resolve(status.value());
        return ResponseEntity.status(status).body(new ApiError(
                status.value(),
                resolved == null ? status.toString() : resolved.getReasonPhrase(),
                responseMessage(exception),
                request.getRequestURI(),
                null));
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ApiError> typeCatalogUnavailable(
            RestClientException exception, HttpServletRequest request) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Type Catalog service is unavailable.", request, null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> internal(RuntimeException exception, HttpServletRequest request) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(), request, null);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request,
                                           Map<String, String> validationErrors) {
        return ResponseEntity.status(status).body(new ApiError(
                status.value(), status.getReasonPhrase(), message, request.getRequestURI(), validationErrors));
    }

    private String responseMessage(RestClientResponseException exception) {
        try {
            JsonNode body = objectMapper.readTree(exception.getResponseBodyAsByteArray());
            JsonNode message = body == null ? null : body.get("message");
            if (message != null && message.isTextual()) {
                return message.asText();
            }
        } catch (IOException ignored) {
            return exception.getStatusText();
        }
        return exception.getStatusText();
    }

    public record ApiError(int status, String error, String message, String path,
                           Map<String, String> validationErrors) {
    }
}
