package ru.practicum.main_service.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.dto.UpdateCommentDto;
import ru.practicum.main_service.comment.mapper.CommentMapper;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.comment.repository.CommentRepository;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.exception.DataConflictException;
import ru.practicum.main_service.exception.DataNotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final UserService userService;
    private final EventService eventService;
    private final CommentRepository repository;

    @Override
    public List<CommentDto> getComments(Long userId, Long eventId, Pageable pageable) {
        if (userId == null && eventId == null) {
            return repository.findAll(pageable).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        }
        if (userId != null) {
            userService.getUserById(userId);

            List<Comment> comments;
            if (eventId != null) {
                eventService.getEventById(eventId);

                comments = repository.findAllByAuthorIdAndEventId(userId, eventId);
            } else {
                comments = repository.findAllByAuthorId(userId);
            }
            return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        } else {
            eventService.getEventById(eventId);

            return repository.findAllByEventId(eventId, pageable).stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
        }
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        Comment comment = repository.findById(commentId).orElseThrow(() ->
                new DataNotFoundException(String.format("Комментарий с id = %d не найден", commentId)));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    @Transactional
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User user = userService.getUserById(userId);
        Event event = eventService.getEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.error("Нельзя оставить комментарий к еще не опубликованным событиям");
            throw new DataConflictException("Нельзя оставить комментарий к еще не опубликованным событиям");
        }

        Comment comment = CommentMapper.toComment(newCommentDto, user, event, LocalDateTime.now());

        return CommentMapper.toCommentDto(repository.save(comment));
    }

    @Override
    public CommentDto edit(Long userId, Long commentId, UpdateCommentDto updateCommentDto) {
        userService.getUserById(userId);

        Comment oldComment = repository.findById(commentId).orElseThrow(() ->
                new DataNotFoundException(String.format("Комментарий с id = %d не найден", commentId)));

        checkUserIsOwner(oldComment.getAuthor().getId(), userId);

        oldComment.setText(updateCommentDto.getText());
        oldComment.setEditedOn(LocalDateTime.now());

        return CommentMapper.toCommentDto(repository.save(oldComment));
    }

    @Override
    public void delete(Long userId, Long commentId) {
        if (userId == null) {
            repository.deleteById(commentId);
        } else {
            userService.getUserById(userId);
            Comment comment = repository.findById(commentId).orElseThrow(() ->
                    new DataNotFoundException(String.format("Комментарий с id = %d не найден", commentId)));

            checkUserIsOwner(comment.getAuthor().getId(), userId);

            repository.deleteById(commentId);
        }
    }

    private void checkUserIsOwner(Long commentUserId, Long requestUserId) {
        if (!Objects.equals(commentUserId, requestUserId)) {
            log.error("Пользователь с id = {} не является владельцем комментария", requestUserId);
            throw new DataConflictException(String.format("Пользователь с id = %d не является владельцем комментария", requestUserId));
        }
    }
}
