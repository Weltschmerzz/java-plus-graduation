package ru.practicum.ewm.requests.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class EventRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getEventRequests(@PathVariable long userId,
                                                          @PathVariable long eventId) {
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping
    public EventRequestStatusUpdateResult updateEventRequestsStatus(
            @PathVariable long userId,
            @PathVariable long eventId,
            @Valid @RequestBody EventRequestStatusUpdateRequest request) {
        return requestService.updateEventRequestsStatus(userId, eventId, request);
    }
}
