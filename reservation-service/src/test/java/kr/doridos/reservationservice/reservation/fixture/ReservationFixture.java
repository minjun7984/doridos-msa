package kr.doridos.reservationservice.reservation.fixture;

import kr.doridos.reservationservice.reservation.entity.Reservation;
import kr.doridos.reservationservice.reservation.entity.ReservationStatus;

import java.util.List;

public class ReservationFixture {
    public static Reservation 예매생성() {
        return Reservation.builder()
                .id(1L)
                .scheduleId(1L)
                .seatId(1L)
                .reservationStatus(ReservationStatus.PAYMENT_WAITING)
                .ticketId(1L)
                .userId(1L)
                .build();
    }
}
