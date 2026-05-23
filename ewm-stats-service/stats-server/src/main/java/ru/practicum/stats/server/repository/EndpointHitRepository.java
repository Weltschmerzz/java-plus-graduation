package ru.practicum.stats.server.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.stats.server.model.EndpointHit;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {


    @Query(value = """
            SELECT app AS app, uri AS uri, COUNT(*) AS hits
            FROM endpoint_hit
            WHERE hit_time BETWEEN :start AND :end
            GROUP BY app, uri
            ORDER BY hits DESC
            """, nativeQuery = true)
    List<ViewStatsProjection> findStats(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);

    @Query(value = """
            SELECT app AS app, uri AS uri, COUNT(DISTINCT ip) AS hits
            FROM endpoint_hit
            WHERE hit_time BETWEEN :start AND :end
            GROUP BY app, uri
            ORDER BY hits DESC
            """, nativeQuery = true)
    List<ViewStatsProjection> findUniqueStats(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end);


    @Query(value = """
            SELECT app AS app, uri AS uri, COUNT(*) AS hits
            FROM endpoint_hit
            WHERE hit_time BETWEEN :start AND :end
              AND uri IN (:uris)
            GROUP BY app, uri
            ORDER BY hits DESC
            """, nativeQuery = true)
    List<ViewStatsProjection> findStatsByUris(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("uris") List<String> uris);

    @Query(value = """
            SELECT app AS app, uri AS uri, COUNT(DISTINCT ip) AS hits
            FROM endpoint_hit
            WHERE hit_time BETWEEN :start AND :end
              AND uri IN (:uris)
            GROUP BY app, uri
            ORDER BY hits DESC
            """, nativeQuery = true)
    List<ViewStatsProjection> findUniqueStatsByUris(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end,
                                                    @Param("uris") List<String> uris);
}
