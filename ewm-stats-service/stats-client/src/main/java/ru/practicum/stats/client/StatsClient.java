package ru.practicum.stats.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats.dto.EndpointHitDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    private static final String API_PREFIX = "";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
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

    private Map<String, Object> createParameters(LocalDateTime start, LocalDateTime end,
                                                 List<String> uris, Boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        parameters.put("end", end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", uris.toArray());
        }

        if (unique != null) {
            parameters.put("unique", unique);
        }

        return parameters;
    }
}
