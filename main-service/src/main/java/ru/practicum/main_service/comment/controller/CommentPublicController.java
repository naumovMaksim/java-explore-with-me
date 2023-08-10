package ru.practicum.main_service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsByPublic(@RequestParam Long eventId,
                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero Integer from,
                                                @RequestParam(required = false, defaultValue = "10") @Positive Integer size) {
        log.info("Пришел GET публичный запрос на получение всех комментариев события с id = {}", eventId);
        List<CommentDto> comments = commentService.getComments(null, eventId, PageRequest.of(from / size, size));
        log.info("Ответ отправлен: {}", comments);
        return comments;
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByIdByPublic(@PathVariable Long commentId) {
        log.info("Пришел GET публичный запрос на получение коментария с id = {}", commentId);
        CommentDto comment = commentService.getCommentById(commentId);
        log.info("Ответ отправлен: {}", comment);
        return comment;
    }
}
