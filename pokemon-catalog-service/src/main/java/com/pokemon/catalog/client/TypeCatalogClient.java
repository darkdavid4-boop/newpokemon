package com.pokemon.catalog.client;

import com.pokemon.catalog.dto.TypeDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
public class TypeCatalogClient {

    private final RestClient restClient;

    public TypeCatalogClient(RestClient.Builder builder,
                             @Value("${services.type-catalog.base-url:http://localhost:8081}") String baseUrl) {
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(2))
                        .build());
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = builder.clone().baseUrl(baseUrl).requestFactory(requestFactory).build();
    }

    public TypeDefinition resolve(String identifier) {
        return restClient.get()
                .uri("/internal/v1/types/resolve/{identifier}", identifier)
                .retrieve()
                .body(TypeDefinition.class);
    }

    public List<TypeDefinition> findAll() {
        List<TypeDefinition> types = restClient.get()
                .uri("/internal/v1/types")
                .retrieve()
                .body(new ParameterizedTypeReference<>() { });
        if (types == null) {
            throw new IllegalStateException("Type Catalog returned an empty response.");
        }
        return types;
    }
}
