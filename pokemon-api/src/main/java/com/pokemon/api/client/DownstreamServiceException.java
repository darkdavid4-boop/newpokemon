package com.pokemon.api.client;

import org.springframework.http.HttpStatusCode;

public class DownstreamServiceException extends RuntimeException {

    private final HttpStatusCode status;

    public DownstreamServiceException(HttpStatusCode status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public HttpStatusCode getStatus() {
        return status;
    }
}
