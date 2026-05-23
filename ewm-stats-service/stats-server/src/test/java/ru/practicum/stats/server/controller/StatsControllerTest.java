package ru.practicum.stats.server.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;
import ru.practicum.stats.server.repository.EndpointHitRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatsControllerTest {

    private static final String DATE_TIME_1 = "2024-01-15 10:00:00";
    private static final String RANGE_START = "2024-01-01 00:00:00";
    private static final String RANGE_END = "2024-02-01 00:00:00";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EndpointHitRepository endpointHitRepository;

    @BeforeEach
    void setUp() {
        endpointHitRepository.deleteAll();
    }

    @Test
    void hit_shouldReturnCreated_andPersistRow() throws Exception {
        EndpointHitDto dto = new EndpointHitDto(
                "ewm-main-service",
                "/events/1",
                "192.168.1.1",
                DATE_TIME_1
        );

        mockMvc.perform(post("/hit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());

        assertEquals(1, endpointHitRepository.count());

        List<EndpointHit> saved = endpointHitRepository.findAll();
        assertEquals(1, saved.size());

        EndpointHit hit = saved.getFirst();
        assertNotNull(hit.getId());
        assertEquals("ewm-main-service", hit.getApp());
        assertEquals("/events/1", hit.getUri());
        assertEquals("192.168.1.1", hit.getIp());
        assertEquals(LocalDateTime.of(2024, 1, 15, 10, 0), hit.getHitTime());
    }

    @Test
    void getStats_shouldAggregateHits_andSupportUnique_andUrisFilter() throws Exception {
        // /events/1 -> 3 hits (2 unique IPs)
        saveEntity("/events/1", "192.168.1.1",
                LocalDateTime.of(2024, 1, 15, 10, 0));
        saveEntity("/events/1", "192.168.1.2",
                LocalDateTime.of(2024, 1, 15, 10, 5));
        saveEntity("/events/1", "192.168.1.1",
                LocalDateTime.of(2024, 1, 15, 10, 10));

        // /events/2 -> 1 hit
        saveEntity("/events/2", "192.168.1.1",
                LocalDateTime.of(2024, 1, 15, 10, 10));

        // Non-unique: must return both URIs ordered by hits desc
        mockMvc.perform(get("/stats")
                        .param("start", RANGE_START)
                        .param("end", RANGE_END))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].app", is("ewm-main-service")))
                .andExpect(jsonPath("$[0].uri", is("/events/1")))
                .andExpect(jsonPath("$[0].hits", is(3)))
                .andExpect(jsonPath("$[1].uri", is("/events/2")))
                .andExpect(jsonPath("$[1].hits", is(1)));

        // Unique: /events/1 must become 2
        mockMvc.perform(get("/stats")
                        .param("start", RANGE_START)
                        .param("end", RANGE_END)
                        .param("unique", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].uri", is("/events/1")))
                .andExpect(jsonPath("$[0].hits", is(2)));

        // URIs filter: only /events/2
        mockMvc.perform(get("/stats")
                        .param("start", RANGE_START)
                        .param("end", RANGE_END)
                        .param("uris", "/events/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uri", is("/events/2")))
                .andExpect(jsonPath("$[0].hits", is(1)));
    }

    private void saveEntity(String uri, String ip, LocalDateTime hitTime) {
        EndpointHit hit = new EndpointHit();
        hit.setApp("ewm-main-service");
        hit.setUri(uri);
        hit.setIp(ip);
        hit.setHitTime(hitTime);
        endpointHitRepository.save(hit);
    }
}


