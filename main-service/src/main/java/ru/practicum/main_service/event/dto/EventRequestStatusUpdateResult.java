package ru.practicum.main_service.event.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {
    @NotNull
    private List<ParticipationRequestDto> confirmedRequests;
    @NotNull
    private List<ParticipationRequestDto> rejectedRequests;
}
