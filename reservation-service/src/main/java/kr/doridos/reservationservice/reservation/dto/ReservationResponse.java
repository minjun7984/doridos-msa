package kr.doridos.reservationservice.reservation.dto;

import kr.doridos.reservationservice.reservation.entity.Reservation;
import kr.doridos.reservationservice.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long reservationId,
        Long scheduleId,
        Long ticketId,
        String ticketTitle,
        String seatSection,
        int seatNum,
        LocalDateTime startDate,
        ReservationStatus status
) {

    public static List<ReservationResponse> of(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponse(
                        reservation.getId(),
                        reservation.getScheduleId(),
                        reservation.getTicketId(),
                        reservation.getTicketTitle(),
                        reservation.getSeatSection(),
                        reservation.getSeatNum(),
                        reservation.getStartDate(),
                        reservation.getReservationStatus()
                ))
                .toList();
    }
}
