package ru.practicum.ewm.requests.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.api.EventLookupService;
import ru.practicum.ewm.events.api.EventParticipationInfo;

@Service
@Primary
@RequiredArgsConstructor
@ConditionalOnProperty(name = "ewm.events.lookup.mode", havingValue = "feign")
public class FeignEventLookupService implements EventLookupService {

    private final EventServiceClient eventServiceClient;

    @Override
    public EventParticipationInfo getParticipationInfo(Long eventId) {
        return eventServiceClient.getParticipationInfo(eventId);
    }
}
