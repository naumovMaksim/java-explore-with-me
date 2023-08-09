package ru.practicum.main_service.user.service;

import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(NewUserRequest newUserRequest);

    List<UserDto> get(List<Long> ids, Pageable pageable);

    void deleteById(Long id);

    User getUserById(Long id);

    List<User> getAll();
}
