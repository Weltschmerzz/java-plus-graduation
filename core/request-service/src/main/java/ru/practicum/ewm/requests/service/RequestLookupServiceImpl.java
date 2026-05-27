package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.requests.api.RequestLookupService;
import ru.practicum.ewm.requests.model.RequestStatus;
import ru.practicum.ewm.requests.repository.ParticipationRequestRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RequestLookupServiceImpl implements RequestLookupService {

    private final ParticipationRequestRepository requestRepository;

    @Override
    public long countConfirmedByEventId(Long eventId) {
        return requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
    }

    @Override
    public Map<Long, Long> countConfirmedByEventIds(Collection<Long> eventIds) {
        if (eventIds == null || eventIds.isEmpty()) {
            return Map.of();
        }

        Map<Long, Long> result = new HashMap<>();
        for (Object[] row : requestRepository.countByEventIdsAndStatus(eventIds, RequestStatus.CONFIRMED)) {
            result.put((Long) row[0], (Long) row[1]);
        }
        return result;
    }
}
