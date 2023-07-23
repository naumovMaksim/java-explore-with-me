package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.Stats;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC ")
    List<ViewStats> getWithUnique(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC ")
    List<ViewStats> getAll(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN (?3)" +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC ")
    List<ViewStats> getWithUniqueAndUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.model.ViewStats(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN (?3)" +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC ")
    List<ViewStats> getUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
