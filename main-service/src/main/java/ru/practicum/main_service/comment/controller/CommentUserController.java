package ru.practicum.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.dto.UpdateCommentDto;
import ru.practicum.main_service.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/comments")
@Slf4j
public class CommentUserController {
    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createByUser(@PathVariable Long userId,
                                   @RequestParam Long eventId,
                                   @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Пришел POST запрос от пользователя с id = {} на добавление нового комментария к событию с id = {}" +
                ", комментарий: {}", userId, eventId, newCommentDto);
        CommentDto comment = commentService.create(userId, eventId, newCommentDto);
        log.info("Ответ отправлен: {}", comment);
        return comment;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByUser(@PathVariable Long userId,
                                              @RequestParam(required = false) Long eventId,
                                              @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                              @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришел GET запрос от пользователя с id = {}, на получение комментариев события с id = {}" +
                " и с параметрами: from = {}, size = {}", userId, eventId, from, size);
        List<CommentDto> comments = commentService.getComments(userId, eventId, PageRequest.of(from / size, size));
        log.info("Ответ отправлен: {}", comments);
        return comments;
    }

    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto editByUser(@PathVariable Long userId,
                                 @PathVariable Long commentId,
                                 @Valid @RequestBody UpdateCommentDto updateCommentDto) {
        log.info("Пришел PATCH запрос от пользователя с id = {} на изменение комментария с id = {} на: {}",
                userId, commentId, updateCommentDto);
        CommentDto comment = commentService.edit(userId, commentId, updateCommentDto);
        log.info("Ответ отправлен: {}", comment);
        return comment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByUser(@PathVariable Long userId,
                             @PathVariable Long commentId) {
        log.info("Пришел DELETE запрос от пользователя c id = {} на удаление комментария с id = {}", userId, commentId);
        commentService.delete(userId, commentId);
        log.info("Комментарий с id = {} удален пользователем c id = {}", commentId, userId);
    }
}
