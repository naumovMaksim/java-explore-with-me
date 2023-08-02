package ru.practicum.main_service.event.mapper;

import ru.practicum.main_service.event.dto.LocationDto;
import ru.practicum.main_service.event.model.Location;

public class LocationMapper {
    public static Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lon(locationDto.getLon())
                .lat(locationDto.getLat())
                .build();
    }

    public static LocationDto toLocationDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
