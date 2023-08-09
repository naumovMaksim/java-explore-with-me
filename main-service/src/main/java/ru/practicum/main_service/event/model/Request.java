package ru.practicum.main_service.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.main_service.event.enums.RequestStatus;
import ru.practicum.main_service.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "REQUESTS", schema = "public")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User requester;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Event event;

    @Column
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column
    private LocalDateTime created;


}
