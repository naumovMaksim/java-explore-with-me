package ru.practicum.main_service.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    @NotBlank
    @Email
    private String email;

    @NotNull
    private Long id;

    @NotBlank
    private String name;
}
