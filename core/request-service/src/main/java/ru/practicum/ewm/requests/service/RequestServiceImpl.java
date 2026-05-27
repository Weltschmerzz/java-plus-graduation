package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.api.EventLookupService;
import ru.practicum.ewm.events.api.EventParticipationInfo;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.requests.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.requests.dto.ParticipationRequestDto;
import ru.practicum.ewm.requests.mapper.RequestMapper;
import ru.practicum.ewm.requests.model.ParticipationRequest;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.requests.model.RequestUpdateStatus;
import ru.practicum.ewm.requests.repository.ParticipationRequestRepository;
import ru.practicum.ewm.users.api.UserLookupService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private static final String PUBLISHED_STATE = "PUBLISHED";

    private final UserLookupService userLookupService;
    private final EventLookupService eventLookupService;
    private final ParticipationRequestRepository requestRepository;

    @Override
    @Transactional
    public ParticipationRequestDto addParticipationRequest(long userId, long eventId) {
        userLookupService.requireExists(userId);

        EventParticipationInfo event = eventLookupService.getParticipationInfo(eventId);

        if (Objects.equals(event.getInitiatorId(), userId)) {
            throw new ConflictException("Event initiator cannot request participation in own event");
        }

        if (!PUBLISHED_STATE.equals(event.getState())) {
            throw new ConflictException("Participation requests are allowed only for published events");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Participation request already exists");
        }

        if (event.getParticipantLimit() > 0) {
            long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (confirmed >= event.getParticipantLimit()) {
                throw new ConflictException("Participant limit has been reached");
            }
        }

        ParticipationRequest pr = new ParticipationRequest();
        pr.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        pr.setEventId(eventId);
        pr.setRequesterId(userId);

        if (event.getParticipantLimit() == 0 || !Boolean.TRUE.equals(event.getRequestModeration())) {
            pr.setStatus(RequestStatus.CONFIRMED);
        } else {
            pr.setStatus(RequestStatus.PENDING);
        }

        ParticipationRequest saved = requestRepository.save(pr);
        return RequestMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getUserRequests(long userId) {
        userLookupService.requireExists(userId);

        return requestRepository.findAllByRequesterIdOrderByIdAsc(userId)
                .stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {
        userLookupService.requireExists(userId);

        ParticipationRequest pr = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));

        pr.setStatus(RequestStatus.CANCELED);
        ParticipationRequest saved = requestRepository.save(pr);

        return RequestMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParticipationRequestDto> getEventRequests(long userId, long eventId) {
        userLookupService.requireExists(userId);

        EventParticipationInfo event = eventLookupService.getParticipationInfo(eventId);
        if (!Objects.equals(event.getInitiatorId(), userId)) {
            throw new NotFoundException("Event with id=" + userId + " not found");
        }

        return requestRepository.findAllByEventIdOrderByIdAsc(eventId)
                .stream()
                .map(RequestMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateEventRequestsStatus(long userId,
                                                                    long eventId,
                                                                    EventRequestStatusUpdateRequest dto) {
        userLookupService.requireExists(userId);

        EventParticipationInfo event = eventLookupService.getParticipationInfo(eventId);
        if (!Objects.equals(event.getInitiatorId(), userId)) {
            throw new NotFoundException("Event with id=" + userId + " not found");
        }

        List<ParticipationRequest> requests = requestRepository.findAllByIdIn(dto.getRequestIds());
        if (requests.size() != dto.getRequestIds().size()) {
            throw new NotFoundException("Not all requests were found");
        }

        for (ParticipationRequest request : requests) {
            if (!Objects.equals(request.getEventId(), eventId)) {
                throw new ConflictException("Request does not belong to the specified event");
            }
            if (request.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Only pending requests can be updated");
            }
        }

        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        if (event.getParticipantLimit() == 0 || !Boolean.TRUE.equals(event.getRequestModeration())) {
            for (ParticipationRequest request : requests) {
                if (dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    result.getConfirmedRequests().add(RequestMapper.toDto(request));
                } else {
                    request.setStatus(RequestStatus.REJECTED);
                    result.getRejectedRequests().add(RequestMapper.toDto(request));
                }
            }
            requestRepository.saveAll(requests);
            return result;
        }

        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int limit = event.getParticipantLimit();

        if (dto.getStatus() == RequestUpdateStatus.CONFIRMED) {
            for (ParticipationRequest request : requests) {
                if (confirmed >= limit) {
                    throw new ConflictException("Participant limit has been reached");
                }
                request.setStatus(RequestStatus.CONFIRMED);
                confirmed++;
                result.getConfirmedRequests().add(RequestMapper.toDto(request));
            }
            requestRepository.saveAll(requests);

            if (confirmed >= limit) {
                List<ParticipationRequest> pending = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                for (ParticipationRequest request : pending) {
                    request.setStatus(RequestStatus.REJECTED);
                }
                requestRepository.saveAll(pending);
            }

            return result;
        }

        for (ParticipationRequest request : requests) {
            request.setStatus(RequestStatus.REJECTED);
            result.getRejectedRequests().add(RequestMapper.toDto(request));
        }
        requestRepository.saveAll(requests);
        return result;
    }
}
