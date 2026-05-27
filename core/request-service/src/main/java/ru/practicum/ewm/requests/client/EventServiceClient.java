package ru.practicum.ewm.requests.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.events.api.EventParticipationInfo;

@FeignClient(name = "event-service", path = "/internal/events")
public interface EventServiceClient {

    @GetMapping("/{eventId}/participation-info")
    EventParticipationInfo getParticipationInfo(@PathVariable Long eventId);
}
