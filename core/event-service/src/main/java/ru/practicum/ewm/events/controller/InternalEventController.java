package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.api.EventLookupService;
import ru.practicum.ewm.events.api.EventParticipationInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/events")
public class InternalEventController {

    private final EventLookupService eventLookupService;

    @GetMapping("/{eventId}/participation-info")
    public EventParticipationInfo getParticipationInfo(@PathVariable Long eventId) {
        return eventLookupService.getParticipationInfo(eventId);
    }
}
