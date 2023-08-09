package ru.practicum.main_service.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserShortDto {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
}
