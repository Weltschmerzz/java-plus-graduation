package ru.practicum.ewm.requests.api;

import java.util.Collection;
import java.util.Map;

public interface RequestLookupService {

    long countConfirmedByEventId(Long eventId);

    Map<Long, Long> countConfirmedByEventIds(Collection<Long> eventIds);
}
