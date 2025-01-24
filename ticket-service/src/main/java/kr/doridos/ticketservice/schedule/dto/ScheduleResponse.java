package kr.doridos.ticketservice.schedule.dto;

import kr.doridos.ticketservice.schedule.entity.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record ScheduleResponse(Long id, LocalDateTime startTime, LocalDateTime endTime) {

    public static ScheduleResponse fromEntity(final Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getStartDate(),
                schedule.getEndDate()
        );
    }

    public static List<ScheduleResponse> from(final List<Schedule> schedules) {
        return schedules.stream()
                .map(ScheduleResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
