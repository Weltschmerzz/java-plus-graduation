package ru.practicum.stats.server.service;

import java.time.LocalDateTime;
import java.util.List;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;

public interface StatsService {
    void saveHit(EndpointHitDto endpointHitDto);

    List<ViewStatsDto> getStats(LocalDateTime start,
                                LocalDateTime end,
                                List<String> uris,
                                boolean unique);
}
