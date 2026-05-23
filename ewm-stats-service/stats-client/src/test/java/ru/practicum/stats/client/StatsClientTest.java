package ru.practicum.stats.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHitDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsClientTest {

    @Mock
    private DiscoveryClient discoveryClient;
    @Mock
    private ServiceInstance serviceInstance;
    @Mock
    private RestTemplate restTemplate;
    private StatsClient statsClient;

    @BeforeEach
    void setUp() {
        lenient().when(serviceInstance.getUri()).thenReturn(URI.create("http://stats-host:9090"));
        when(discoveryClient.getInstances("stats-server")).thenReturn(List.of(serviceInstance));
        statsClient = new StatsClient(discoveryClient, restTemplate);
    }

    @Test
    void saveHit_shouldWork() {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app("test")
                .uri("/test")
                .ip("127.0.0.1")
                .timestamp("2024-01-15 10:00:00")
                .build();

        when(restTemplate.exchange(
                eq("http://stats-host:9090/hit"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(ResponseEntity.ok().build());

        ResponseEntity<Object> response = statsClient.saveHit(hitDto);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getStats_shouldCallCorrectEndpoint() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        ResponseEntity<Object> response = statsClient.getStats(start, end, null, null);

        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(restTemplate).exchange(
                eq("http://stats-host:9090/stats?start={start}&end={end}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void getStats_withUris_shouldWork() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);
        List<String> uris = List.of("/events", "/events/1");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(ResponseEntity.ok(List.of()));

        ResponseEntity<Object> response = statsClient.getStats(start, end, uris, true);

        assertNotNull(response);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(restTemplate).exchange(
                eq("http://stats-host:9090/stats?start={start}&end={end}&uris={uris}&unique={unique}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        );
    }

    @Test
    void getStats_shouldHandleServerError() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 1, 31, 23, 59);

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(Object.class),
                any(Map.class)
        )).thenThrow(new org.springframework.web.client.HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "Bad Request"
        ));

        ResponseEntity<Object> response = statsClient.getStats(start, end, null, null);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void saveHit_shouldThrowWhenStatsServerIsNotRegistered() {
        when(discoveryClient.getInstances("stats-server")).thenReturn(Collections.emptyList());

        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app("test")
                .uri("/test")
                .ip("127.0.0.1")
                .timestamp("2024-01-15 10:00:00")
                .build();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> statsClient.saveHit(hitDto)
        );

        assertEquals("stats-server is not registered in Eureka", exception.getMessage());
        verifyNoInteractions(restTemplate);
    }
}
