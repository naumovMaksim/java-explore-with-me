package ru.practicum.main_service.comment.mapper;

import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.user.mapper.UserMapper;
import ru.practicum.main_service.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toComment(NewCommentDto newCommentDto, User user, Event event, LocalDateTime createdOn) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .author(user)
                .event(event)
                .createdOn(createdOn)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .createdOn(comment.getCreatedOn())
                .editedOn(comment.getEditedOn())
                .build();
    }
}
