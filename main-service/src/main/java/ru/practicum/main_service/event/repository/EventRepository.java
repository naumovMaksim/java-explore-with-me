package ru.practicum.main_service.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.main_service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.initiator.id IN (?1) " +
            "AND e.category.id IN (?2) " +
            "AND e.eventDate between ?3 and ?4")
    List<Event> findAllByAdmin(List<Long> users, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                               Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.annotation = ?1 " +
            "OR e.description = ?1 " +
            "AND e.category.id IN (?2)" +
            "AND e.paid = (?3) " +
            "AND e.eventDate between ?4 and ?5")
    List<Event> findAllByUser(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                              LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.category.id IN (?1) " +
            "AND e.paid = (?2) " +
            "AND e.eventDate between ?3 and ?4")
    List<Event> findAllByUserWithoutText(List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.annotation = ?1 " +
            "OR e.description = ?1 " +
            "AND e.category.id IN (?2)" +
            "AND e.eventDate between ?3 and ?4")
    List<Event> findAllByUserWithoutPaid(String text, List<Long> categories, LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd, Pageable pageable);

    @Query("SELECT e " +
            "FROM Event AS e " +
            "WHERE e.category.id IN (?1)" +
            "AND e.eventDate between ?2 and ?3")
    List<Event> findAllByUserWithoutTextAndPaid(List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                Pageable pageable);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByIdIn(List<Long> eventsId);
}
