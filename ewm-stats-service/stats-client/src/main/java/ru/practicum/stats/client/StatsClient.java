package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    private static final String STATS_SERVER_SERVICE_ID = "stats-server";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(DiscoveryClient discoveryClient, RestTemplateBuilder builder) {
        this(discoveryClient, builder.build());
    }

    StatsClient(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        super(restTemplate, () -> resolveStatsServerUri(discoveryClient));
    }

    private static String resolveStatsServerUri(DiscoveryClient discoveryClient) {
        List<ServiceInstance> instances = discoveryClient.getInstances(STATS_SERVER_SERVICE_ID);
        if (instances == null || instances.isEmpty()) {
            throw new IllegalStateException("stats-server is not registered in Eureka");
        }
        return instances.getFirst().getUri().toString();
    }

    public ResponseEntity<Object> saveHit(EndpointHitDto hitDto) {
        return post("/hit", hitDto);
    }

    public ResponseEntity<Object> getStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           Boolean unique) {
        String path = "/stats?start={start}&end={end}";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(FORMATTER));
        parameters.put("end", end.format(FORMATTER));

        if (uris != null && !uris.isEmpty()) {
            // Сервер (Spring) спокойно распарсит "a,b,c" в List<String>
            path += "&uris={uris}";
            parameters.put("uris", String.join(",", uris));
        }

        if (unique != null) {
            path += "&unique={unique}";
            parameters.put("unique", unique);
        }

        return get(path, parameters);
    }
}
