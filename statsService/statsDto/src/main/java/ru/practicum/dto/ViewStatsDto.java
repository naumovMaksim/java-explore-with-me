package ru.practicum.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewStatsDto implements Comparable<ViewStatsDto> {
    private String app;//название сервиса
    private String uri;//URI сервиса
    private Long hits;//колличество просмотров

    @Override
    public int compareTo(ViewStatsDto o) {
        return Long.compare(o.getHits(), hits);
    }
}
