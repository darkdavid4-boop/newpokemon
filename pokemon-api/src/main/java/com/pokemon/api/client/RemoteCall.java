package com.pokemon.api.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.io.IOException;
import java.util.function.Supplier;

@Component
public class RemoteCall {

    private final ObjectMapper objectMapper;

    public RemoteCall(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T execute(Supplier<T> request) {
        try {
            return request.get();
        } catch (RestClientResponseException exception) {
            throw new DownstreamServiceException(exception.getStatusCode(),
                    responseMessage(exception), exception);
        } catch (ResourceAccessException exception) {
            throw new DownstreamServiceException(HttpStatus.SERVICE_UNAVAILABLE,
                    "A dependent catalog service is unavailable.", exception);
        } catch (RestClientException exception) {
            throw new DownstreamServiceException(HttpStatus.BAD_GATEWAY,
                    "A dependent catalog service returned an invalid response.", exception);
        }
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
}
