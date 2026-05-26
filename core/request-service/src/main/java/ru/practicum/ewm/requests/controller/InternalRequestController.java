package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.api.RequestLookupService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/requests")
public class InternalRequestController {

    private final RequestLookupService requestLookupService;

    @GetMapping("/events/{eventId}/confirmed-count")
    public long countConfirmedByEventId(@PathVariable Long eventId) {
        return requestLookupService.countConfirmedByEventId(eventId);
    }

    @PostMapping("/events/confirmed-counts")
    public Map<Long, Long> countConfirmedByEventIds(@RequestBody Collection<Long> eventIds) {
        return requestLookupService.countConfirmedByEventIds(eventIds);
    }
}
