package ru.practicum.main_service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.main_service.category.dto.CategoryDto;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    @NotBlank
    private String annotation;
    @NotNull
    private CategoryDto category;
    @NotNull
    private Long confirmedRequests;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;
    @NotBlank
    private String description;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;
    @NotNull
    private Long id;
    @NotNull
    private UserShortDto initiator;
    @NotNull
    private LocationDto location;
    @NotNull
    private Boolean paid;
    @NotNull
    private Integer participantLimit;
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;
    @NotNull
    private Boolean requestModeration;
    @NotNull
    private EventState state;
    @NotBlank
    private String title;
    @NotNull
    private Long views;
}
