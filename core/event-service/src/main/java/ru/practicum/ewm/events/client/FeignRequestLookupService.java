package ru.practicum.ewm.events.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.requests.api.RequestLookupService;

import java.util.Collection;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ewm.requests.lookup.mode", havingValue = "feign")
public class FeignRequestLookupService implements RequestLookupService {

    private final RequestServiceClient requestServiceClient;

    @Override
    public long countConfirmedByEventId(Long eventId) {
        return requestServiceClient.countConfirmedByEventId(eventId);
    }

    @Override
    public Map<Long, Long> countConfirmedByEventIds(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }
        return requestServiceClient.countConfirmedByEventIds(eventIds);
    }
}
