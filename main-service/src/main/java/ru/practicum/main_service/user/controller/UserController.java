package ru.practicum.main_service.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.user.dto.UserRequestDto;
import ru.practicum.main_service.user.dto.UserResponseDto;
import ru.practicum.main_service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Slf4j
public class UserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@Valid @RequestBody UserRequestDto user) {
        log.info("Пришел POST запрос на создания пользователя {}", user);
        UserResponseDto userResponseDto = service.create(user);
        log.info("Ответ отправлен {}", userResponseDto);
        return userResponseDto;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> get(@RequestParam(required = false) List<Long> ids,
                                     @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                     @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришел GET запрос на получение списка пользователей с параметрами ids = {}, from = {}, size = {}", ids, from, size);
        List<UserResponseDto> users = service.get(ids, PageRequest.of(from / size, size));
        log.info("Ответ отправлен {}", users);
        return users;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        log.info("Пришел DELETE запрос на удаление пользователя с id = {}", userId);
        service.deleteById(userId);
        log.info("Пользователь с id = {} удален", userId);
    }
}
