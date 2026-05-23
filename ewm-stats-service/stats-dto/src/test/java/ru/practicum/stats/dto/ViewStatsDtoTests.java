package ru.practicum.stats.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ViewStatsDtoTests {
    @Test
    void shouldCreateViewStatsDtoWithAllArgsConstructor() {

        String app = "ewm-main-service";
        String uri = "/events/5";
        Long hits = 42L;

        ViewStatsDto viewStatsDto = new ViewStatsDto(app, uri, hits);

        assertNotNull(viewStatsDto);
        assertEquals(app, viewStatsDto.getApp());
        assertEquals(uri, viewStatsDto.getUri());
        assertEquals(hits, viewStatsDto.getHits());
    }

    @Test
    void shouldCreateViewStatsDtoWithBuilder() {
        ViewStatsDto viewStatsDto = ViewStatsDto.builder()
                .app("stats-service")
                .uri("/stats")
                .hits(100L)
                .build();

        assertNotNull(viewStatsDto);
        assertEquals("stats-service", viewStatsDto.getApp());
        assertEquals("/stats", viewStatsDto.getUri());
        assertEquals(100L, viewStatsDto.getHits());
    }

    @Test
    void shouldCreateEmptyViewStatsDtoAndSetValues() {
        ViewStatsDto viewStatsDto = new ViewStatsDto();
        viewStatsDto.setApp("test-app");
        viewStatsDto.setUri("/test/uri");
        viewStatsDto.setHits(999L);

        assertEquals("test-app", viewStatsDto.getApp());
        assertEquals("/test/uri", viewStatsDto.getUri());
        assertEquals(999L, viewStatsDto.getHits());
    }

}
