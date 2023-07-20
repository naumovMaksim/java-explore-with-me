package ru.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "STATS", schema = "public")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Stats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "app_name", nullable = false)
    String app;

    @Column
    String uri;

    @Column(name = "user_ip", length = 15)
    String ip;

    @Column(name = "created")
    LocalDateTime timestamp;
}
