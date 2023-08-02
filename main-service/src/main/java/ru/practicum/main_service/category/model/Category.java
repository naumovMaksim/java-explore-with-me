package ru.practicum.main_service.category.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "CATEGORIES", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    String name;
}
