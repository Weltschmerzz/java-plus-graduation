package ru.practicum.ewm.locations.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LocationEventShortDto {
    private Long id;
    private String title;
    private String annotation;
    private Object category;
    private Object initiator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Boolean paid;
    private Long confirmedRequests;
    private Long views;
}
