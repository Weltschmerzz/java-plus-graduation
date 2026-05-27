package ru.practicum.ewm.events.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventParticipationInfo {

    private Long id;
    private Long initiatorId;
    private String state;
    private Integer participantLimit;
    private Boolean requestModeration;
}
