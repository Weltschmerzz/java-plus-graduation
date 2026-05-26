package ru.practicum.ewm.events.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.Map;

@FeignClient(name = "request-service", path = "/internal/requests")
public interface RequestServiceClient {

    @GetMapping("/events/{eventId}/confirmed-count")
    long countConfirmedByEventId(@PathVariable Long eventId);

    @PostMapping("/events/confirmed-counts")
    Map<Long, Long> countConfirmedByEventIds(@RequestBody Collection<Long> eventIds);
}
