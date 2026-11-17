package com.pokemon.typecatalog.config;

import com.pokemon.typecatalog.service.DuplicateTypeException;
import com.pokemon.typecatalog.service.TypeNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(TypeNotFoundException.class)
    public ResponseEntity<ApiError> notFound(TypeNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage(), request, null);
    }

    @ExceptionHandler(DuplicateTypeException.class)
    public ResponseEntity<ApiError> conflict(DuplicateTypeException exception, HttpServletRequest request) {
        return error(HttpStatus.CONFLICT, exception.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> invalid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(field -> errors.put(field.getField(), field.getDefaultMessage()));
        return error(HttpStatus.BAD_REQUEST, "Errores de validación en los datos enviados", request, errors);
    }

    private ResponseEntity<ApiError> error(HttpStatus status, String message, HttpServletRequest request,
                                           Map<String, String> validationErrors) {
        return ResponseEntity.status(status).body(new ApiError(
                status.value(), status.getReasonPhrase(), message, request.getRequestURI(), validationErrors));
    }

    public record ApiError(int status, String error, String message, String path,
                           Map<String, String> validationErrors) {
    }
}
