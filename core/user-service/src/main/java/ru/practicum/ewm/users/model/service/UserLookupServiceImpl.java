package ru.practicum.ewm.users.model.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.events.dto.UserShortDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.users.api.UserLookupService;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserLookupServiceImpl implements UserLookupService {

    private final UserRepository userRepository;

    @Override
    public void requireExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден!");
        }
    }

    @Override
    public UserShortDto getShortById(Long userId) {
        return userRepository.findById(userId)
                .map(this::toShortDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден!"));
    }

    @Override
    public Map<Long, UserShortDto> getShortByIds(Collection<Long> userIds) {
        Map<Long, UserShortDto> result = new HashMap<>();
        if (userIds == null || userIds.isEmpty()) {
            return result;
        }

        for (User user : userRepository.findAllById(userIds)) {
            result.put(user.getId(), toShortDto(user));
        }
        return result;
    }

    private UserShortDto toShortDto(User user) {
        UserShortDto dto = new UserShortDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }
}
