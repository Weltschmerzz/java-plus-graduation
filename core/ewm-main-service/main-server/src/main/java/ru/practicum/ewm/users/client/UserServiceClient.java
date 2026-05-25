package ru.practicum.ewm.users.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.ewm.events.dto.UserShortDto;

import java.util.Collection;
import java.util.Map;

@FeignClient(name = "user-service", path = "/internal/users")
public interface UserServiceClient {

    @GetMapping("/{userId}/exists")
    void requireExists(@PathVariable Long userId);

    @GetMapping("/{userId}/short")
    UserShortDto getShortById(@PathVariable Long userId);

    @PostMapping("/short")
    Map<Long, UserShortDto> getShortByIds(@RequestBody Collection<Long> userIds);
}
