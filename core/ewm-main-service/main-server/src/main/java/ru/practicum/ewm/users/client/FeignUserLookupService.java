package ru.practicum.ewm.users.client;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.UserShortDto;
import ru.practicum.ewm.users.api.UserLookupService;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Service
@Primary
@RequiredArgsConstructor
public class FeignUserLookupService implements UserLookupService {

    private final UserServiceClient userServiceClient;

    @Override
    public void requireExists(Long userId) {
        userServiceClient.requireExists(userId);
    }

    @Override
    public UserShortDto getShortById(Long userId) {
        return userServiceClient.getShortById(userId);
    }

    @Override
    public Map<Long, UserShortDto> getShortByIds(Collection<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return userServiceClient.getShortByIds(userIds);
    }
}
