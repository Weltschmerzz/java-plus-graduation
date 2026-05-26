package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.api.EventLookupService;
import ru.practicum.ewm.events.api.EventParticipationInfo;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class EventLookupServiceImpl implements EventLookupService {

    private final EventRepository eventRepository;

    @Override
    public EventParticipationInfo getParticipationInfo(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        return new EventParticipationInfo(
                event.getId(),
                event.getInitiatorId(),
                event.getState().name(),
                event.getParticipantLimit(),
                event.getRequestModeration()
        );
    }
}
