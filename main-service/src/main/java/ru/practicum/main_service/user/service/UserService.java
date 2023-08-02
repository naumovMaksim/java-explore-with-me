package ru.practicum.main_service.user.service;

import ru.practicum.main_service.user.dto.UserRequestDto;
import ru.practicum.main_service.user.dto.UserResponseDto;
import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.user.model.User;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserRequestDto userRequestDto);

    List<UserResponseDto> get(List<Long> ids, Pageable pageable);

    void deleteById(Long id);

    User getUserById(Long id);
}
