package ru.practicum.stats.server.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.server.model.EndpointHit;

public final class EndpointHitMapper {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private EndpointHitMapper() {
    }

    public static EndpointHit toEntity(EndpointHitDto dto) {
        EndpointHit hit = new EndpointHit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setHitTime(LocalDateTime.parse(dto.getTimestamp(), FORMATTER));
        return hit;
    }

    public static EndpointHitDto toDto(EndpointHit entity) {
        return EndpointHitDto.builder()
                .id(entity.getId())
                .app(entity.getApp())
                .uri(entity.getUri())
                .ip(entity.getIp())
                .timestamp(entity.getHitTime().format(FORMATTER))
                .build();
    }
}
