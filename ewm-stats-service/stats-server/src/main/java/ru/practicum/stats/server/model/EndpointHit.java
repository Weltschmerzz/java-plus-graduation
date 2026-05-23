package ru.practicum.stats.server.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "endpoint_hit")
@Getter
@Setter
public class EndpointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String app;

    @Column(nullable = false, length = 1024)
    private String uri;

    @Column(nullable = false, length = 64)
    private String ip;

    @Column(name = "hit_time", nullable = false)
    private LocalDateTime hitTime;
}
