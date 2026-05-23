package ru.practicum.stats.client;

import java.util.List;
import java.util.Map;

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

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
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

        try {
            if (parameters != null && !parameters.isEmpty()) {
                return rest.exchange(path, method, requestEntity, Object.class, parameters);
            }
            return rest.exchange(path, method, requestEntity, Object.class);
        } catch (HttpStatusCodeException e) {
            // Возвращаем код и тело ошибки как есть — удобно для дебага и корректно для прокси-клиента
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}