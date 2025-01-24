package kr.doridos.ticketservice.schedule.dto;

import kr.doridos.ticketservice.place.entity.Section;
import kr.doridos.ticketservice.schedule.entity.ScheduleSeat;

import java.util.List;
import java.util.stream.Collectors;

public record ScheduleSeatResponse(Long id, int seatNum, Section section,
                                   int price, boolean isReserved) {

    public static ScheduleSeatResponse fromEntity(final ScheduleSeat scheduleSeat) {
        return new ScheduleSeatResponse(
                scheduleSeat.getId(),
                scheduleSeat.getSeatNum(),
                scheduleSeat.getSection(),
                scheduleSeat.getPrice(),
                scheduleSeat.isReserved()
        );
    }

    public static List<ScheduleSeatResponse> from(final List<ScheduleSeat> schedules) {
        return schedules.stream()
                .map(ScheduleSeatResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
