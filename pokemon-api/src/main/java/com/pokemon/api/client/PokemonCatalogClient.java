package com.pokemon.api.client;

import com.pokemon.api.dto.PokemonRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Component
public class PokemonCatalogClient {

    private final RestClient restClient;
    private final RemoteCall remoteCall;

    public PokemonCatalogClient(RestClient.Builder builder,
                                RemoteCall remoteCall,
                                @Value("${services.pokemon-catalog.base-url:http://localhost:8082}") String baseUrl) {
        JdkClientHttpRequestFactory requestFactory =
                new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(2))
                        .build());
        requestFactory.setReadTimeout(Duration.ofSeconds(3));
        this.restClient = builder.clone().baseUrl(baseUrl).requestFactory(requestFactory).build();
        this.remoteCall = remoteCall;
    }

    public PageResponse<PokemonRecord> findAll(String name, String typeCode, Pageable pageable) {
        return remoteCall.execute(() -> restClient.get()
                .uri(uri -> {
                    uri.path("/internal/v1/pokemons")
                            .queryParam("page", pageable.getPageNumber())
                            .queryParam("size", pageable.getPageSize())
                            .queryParam("sortBy", pageable.getSort().stream().findFirst()
                                    .map(order -> order.getProperty()).orElse("pokedexNumber"))
                            .queryParam("direction", pageable.getSort().stream().findFirst()
                                    .map(order -> order.isAscending() ? "asc" : "desc").orElse("asc"));
                    if (name != null) {
                        uri.queryParam("name", name);
                    }
                    if (typeCode != null) {
                        uri.queryParam("typeCode", typeCode);
                    }
                    return uri.build();
                })
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponse<PokemonRecord>>() { }));
    }

    public PokemonRecord findById(Long id) {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/pokemons/{id}", id)
                .retrieve()
                .body(PokemonRecord.class));
    }

    public PokemonRecord findByName(String name) {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/pokemons/name/{name}", name)
                .retrieve()
                .body(PokemonRecord.class));
    }

    public List<PokemonRecord> findByPokedexNumber(Integer number) {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/pokemons/pokedex/{number}", number)
                .retrieve()
                .body(new ParameterizedTypeReference<>() { }));
    }

    public PokemonRecord findRandom() {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/pokemons/random")
                .retrieve()
                .body(PokemonRecord.class));
    }

    public PokemonRecord create(PokemonRequestDTO request) {
        return remoteCall.execute(() -> restClient.post()
                .uri("/internal/v1/pokemons")
                .body(request)
                .retrieve()
                .body(PokemonRecord.class));
    }

    public PokemonRecord update(Long id, PokemonRequestDTO request) {
        return remoteCall.execute(() -> restClient.put()
                .uri("/internal/v1/pokemons/{id}", id)
                .body(request)
                .retrieve()
                .body(PokemonRecord.class));
    }

    public void delete(Long id) {
        remoteCall.execute(() -> {
            restClient.delete().uri("/internal/v1/pokemons/{id}", id).retrieve().toBodilessEntity();
            return null;
        });
    }

    public PageResponse<PokemonRecord> findByTypeCode(String code, Pageable pageable) {
        return remoteCall.execute(() -> restClient.get()
                .uri(uri -> uri.path("/internal/v1/pokemons/by-type/{code}")
                        .queryParam("page", pageable.getPageNumber())
                        .queryParam("size", pageable.getPageSize())
                        .queryParam("sortBy", pageable.getSort().stream().findFirst()
                                .map(order -> order.getProperty()).orElse("pokedexNumber"))
                        .queryParam("direction", pageable.getSort().stream().findFirst()
                                .map(order -> order.isAscending() ? "asc" : "desc").orElse("asc"))
                        .build(code))
                .retrieve()
                .body(new ParameterizedTypeReference<PageResponse<PokemonRecord>>() { }));
    }

    public TypeCounts counts(String code) {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/pokemons/type-counts/{code}", code)
                .retrieve()
                .body(TypeCounts.class));
    }

    public Map<String, TypeCounts> allCounts() {
        return remoteCall.execute(() -> restClient.get()
                .uri("/internal/v1/pokemons/type-counts")
                .retrieve()
                .body(new ParameterizedTypeReference<>() { }));
    }
}
