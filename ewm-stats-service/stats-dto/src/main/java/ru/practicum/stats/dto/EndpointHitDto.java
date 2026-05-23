package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EndpointHitDto {
    private Long id;
    private String app;
    private String uri;
    private String ip;
    private String timestamp;

    // Конструктор, который ожидают тесты без id
    public EndpointHitDto(String app, String uri, String ip, String timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }
}
