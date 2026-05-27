package ru.practicum.ewm.users.api;

import ru.practicum.ewm.events.dto.UserShortDto;

import java.util.Collection;
import java.util.Map;

public interface UserLookupService {

    void requireExists(Long userId);

    UserShortDto getShortById(Long userId);

    Map<Long, UserShortDto> getShortByIds(Collection<Long> userIds);
}
