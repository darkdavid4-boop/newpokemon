package com.pokemon.api.client;

import com.pokemon.api.dto.PokemonTypeRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;

@Component
public class TypeCatalogClient {

    private final RestClient restClient;
    private final RemoteCall remoteCall;

    public TypeCatalogClient(RestClient.Builder builder,
                             RemoteCall remoteCall,
                             @Value("${services.type-catalog.base-url:http://localhost:8081}") String baseUrl) {
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(2))
                        .build());
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = builder.clone().baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.remoteCall = remoteCall;
    }

    public List<TypeDefinition> findAll() {
        List<TypeDefinition> types = remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/types")
                .retrieve()
                .body(new ParameterizedTypeReference<>() { }));
        if (types == null) {
            throw new DownstreamServiceException(
                    org.springframework.http.HttpStatus.BAD_GATEWAY, "Type Catalog returned an empty response.", null);
        }
        return types;
    }

    public TypeDefinition findById(Long id) {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/types/{id}", id)
                .retrieve()
                .body(TypeDefinition.class));
    }

    public TypeDefinition findByName(String name) {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/types/name/{name}", name)
                .retrieve()
                .body(TypeDefinition.class));
    }

    public TypeDefinition resolve(String identifier) {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/types/resolve/{identifier}", identifier)
                .retrieve()
                .body(TypeDefinition.class));
    }

    public TypeDefinition create(PokemonTypeRequestDTO request) {
        return remoteCall.execute(() -> restClient.post()
                .uri("/internal/v1/types")
                .body(new TypeRequest(request.getName(), request.getDescription()))
                .retrieve()
                .body(TypeDefinition.class));
    }

    public TypeDefinition update(Long id, PokemonTypeRequestDTO request) {
        return remoteCall.execute(() -> restClient.put()
                .uri("/internal/v1/types/{id}", id)
                .body(new TypeRequest(request.getName(), request.getDescription()))
                .retrieve()
                .body(TypeDefinition.class));
    }

    public void delete(Long id) {
        remoteCall.execute(() -> {
            restClient.delete().uri("/internal/v1/types/{id}", id).retrieve().toBodilessEntity();
            return null;
        });
    }
}
