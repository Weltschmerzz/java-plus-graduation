package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.dto.UserShortDto;
import ru.practicum.ewm.users.api.UserLookupService;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserLookupService userLookupService;

    @GetMapping("/{userId}/exists")
    public ResponseEntity<Void> requireExists(@PathVariable Long userId) {
        userLookupService.requireExists(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/short")
    public ResponseEntity<UserShortDto> getShortById(@PathVariable Long userId) {
        return ResponseEntity.ok(userLookupService.getShortById(userId));
    }

    @PostMapping("/short")
    public ResponseEntity<Map<Long, UserShortDto>> getShortByIds(@RequestBody Collection<Long> userIds) {
        return ResponseEntity.ok(userLookupService.getShortByIds(userIds));
    }
}
