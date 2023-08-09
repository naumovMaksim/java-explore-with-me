package ru.practicum.main_service.comment.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.dto.UpdateCommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> getComments(Long userId, Long eventId, Pageable pageable);

    CommentDto getCommentById(Long commentId);

    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto edit(Long userId, Long commentId, UpdateCommentDto updateCommentDto);

    void delete(Long userId, Long commentId);
}
