package ru.practicum.stats.client;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.annotation.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {

    protected final RestTemplate rest;
    private final Supplier<String> baseUriSupplier;

    public BaseClient(RestTemplate rest) {
        this(rest, () -> "");
    }

    public BaseClient(RestTemplate rest, Supplier<String> baseUriSupplier) {
        this.rest = rest;
        this.baseUriSupplier = baseUriSupplier;
    }

    protected ResponseEntity<Object> get(String path) {
        return makeAndSendRequest(HttpMethod.GET, path, null, null);
    }

    protected ResponseEntity<Object> get(String path, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected <T> ResponseEntity<Object> post(String path, @Nullable T body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    protected <T> ResponseEntity<Object> post(String path, @Nullable Map<String, Object> parameters, @Nullable T body) {
        return makeAndSendRequest(HttpMethod.POST, path, parameters, body);
    }

    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method,
                                                          String path,
                                                          @Nullable Map<String, Object> parameters,
                                                          @Nullable T body) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());
        String requestPath = resolveRequestPath(path);

        try {
            if (parameters != null && !parameters.isEmpty()) {
                return rest.exchange(requestPath, method, requestEntity, Object.class, parameters);
            }
            return rest.exchange(requestPath, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            // Возвращаем код и тело ошибки как есть — удобно для дебага и корректно для прокси-клиента
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    private String resolveRequestPath(String path) {
        String baseUri = baseUriSupplier.get();
        if (baseUri == null || baseUri.isBlank()) {
            return path;
        }
        String normalizedBaseUri = baseUri.endsWith("/") ? baseUri.substring(0, baseUri.length() - 1) : baseUri;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return normalizedBaseUri + normalizedPath;
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}
