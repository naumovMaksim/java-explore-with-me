package ru.practicum.main_service.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.main_service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorIdInAndCategoryIdInAndEventDateBetween(List<Long> users,
                                                                                 List<Long> categories, LocalDateTime rangeStart,
                                                                                 LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAllByAnnotationOrDescriptionAndCategoryIdInAndPaidAndEventDateBetween(String textAnnotation,
                                                                                       String textDescription,
                                                                                       List<Long> categories, Boolean paid,
                                                                                       LocalDateTime rangeStart,
                                                                                       LocalDateTime rangeEnd, Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByIdIn(List<Long> eventsId);
}
