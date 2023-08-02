package ru.practicum.main_service.event.mapper;

import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.model.Request;

public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEvent().getId())
                .requester(request.getRequester().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }
}
