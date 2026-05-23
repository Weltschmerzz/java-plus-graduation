package ru.practicum.stats.server.service;

import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.mapper.EndpointHitMapper;
import ru.practicum.stats.server.repository.EndpointHitRepository;
import ru.practicum.stats.server.repository.ViewStatsProjection;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        endpointHitRepository.save(EndpointHitMapper.toEntity(endpointHitDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        if (start.isAfter(end)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start должен быть до end");
        }

        boolean hasUris = uris != null && !uris.isEmpty();

        List<ViewStatsProjection> rows;

        if (unique) {
            rows = hasUris
                    ? endpointHitRepository.findUniqueStatsByUris(start, end, uris)
                    : endpointHitRepository.findUniqueStats(start, end);
        } else {
            rows = hasUris
                    ? endpointHitRepository.findStatsByUris(start, end, uris)
                    : endpointHitRepository.findStats(start, end);
        }

        return rows.stream()
                .map(r -> ViewStatsDto.builder()
                        .app(r.getApp())
                        .uri(r.getUri())
                        .hits(r.getHits())
                        .build()
                )
                .toList();
    }
}