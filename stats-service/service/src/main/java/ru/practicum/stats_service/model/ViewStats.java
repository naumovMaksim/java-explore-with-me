package ru.practicum.stats_service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ViewStats {
    @NotBlank
    private String app;
    @NotBlank
    private String uri;
    @NotNull
    private Long hits;
}
