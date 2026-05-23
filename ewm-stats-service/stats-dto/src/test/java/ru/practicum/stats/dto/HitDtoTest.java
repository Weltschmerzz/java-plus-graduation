package ru.practicum.stats.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HitDtoTest {

    @Test
    void shouldCreateHitDtoWithAllArgsConstructor() {
        String app = "ewm-main-service";
        String uri = "/events/1";
        String ip = "192.168.1.1";
        String timestamp = "2024-01-15 10:00:00";

        EndpointHitDto hitDto = new EndpointHitDto(app, uri, ip, timestamp);

        assertNotNull(hitDto);
        assertEquals(app, hitDto.getApp());
        assertEquals(uri, hitDto.getUri());
        assertEquals(ip, hitDto.getIp());
        assertEquals(timestamp, hitDto.getTimestamp());
    }

    @Test
    void shouldCreateHitDtoWithBuilder() {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri("/events")
                .ip("121.0.0.1")
                .timestamp("2024-01-15 11:30:00")
                .build();

        assertNotNull(hitDto);
        assertEquals("ewm-main-service", hitDto.getApp());
        assertEquals("/events", hitDto.getUri());
        assertEquals("121.0.0.1", hitDto.getIp());
        assertEquals("2024-01-15 11:30:00", hitDto.getTimestamp());
    }

    @Test
    void shouldCreateHitDtoWithNoArgsConstructorAndSetters() {
        EndpointHitDto hitDto = new EndpointHitDto();

        hitDto.setApp("stats-service");
        hitDto.setUri("/stats");
        hitDto.setIp("10.0.0.1");
        hitDto.setTimestamp("2024-01-15 12:00:00");

        assertEquals("stats-service", hitDto.getApp());
        assertEquals("/stats", hitDto.getUri());
        assertEquals("10.0.0.1", hitDto.getIp());
        assertEquals("2024-01-15 12:00:00", hitDto.getTimestamp());
    }

    @Test
    void shouldHandleNullValues() {
        EndpointHitDto hitDto = EndpointHitDto.builder()
                .app(null)
                .uri(null)
                .ip(null)
                .timestamp(null)
                .build();

        assertNotNull(hitDto);
        assertNull(hitDto.getApp());
        assertNull(hitDto.getUri());
        assertNull(hitDto.getIp());
        assertNull(hitDto.getTimestamp());
    }
}
