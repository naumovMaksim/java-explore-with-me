package ru.practicum.main_service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.exception.DataNotFoundException;
import ru.practicum.main_service.user.dto.NewUserRequest;
import ru.practicum.main_service.user.dto.UserDto;
import ru.practicum.main_service.user.mapper.UserMapper;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto create(NewUserRequest newUserRequest) {
        User user = repository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserResponseDto(user);
    }

    @Override
    public List<UserDto> get(List<Long> ids, Pageable pageable) {
        if (ids == null || ids.isEmpty()) {
            return repository.findAll(pageable).stream().map(UserMapper::toUserResponseDto).collect(Collectors.toList());
        } else {
            return repository.findAllByIdIn(ids, pageable).stream().map(UserMapper::toUserResponseDto).collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        repository.findById(id).orElseThrow(() -> new DataNotFoundException("Пользователь с таким id не найден"));

        repository.deleteById(id);
    }

    @Override
    public User getUserById(Long id) {
        return repository.findById(id).orElseThrow(() -> new DataNotFoundException("Пользователь с таким id не найден"));
    }

    @Override
    public Long usersCount() {
        return (long) repository.findAll().size();
    }
}
