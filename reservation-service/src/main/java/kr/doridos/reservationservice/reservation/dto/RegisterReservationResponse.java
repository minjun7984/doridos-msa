package kr.doridos.reservationservice.reservation.dto;

import kr.doridos.reservationservice.reservation.entity.Reservation;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterReservationResponse {

    private List<Long> reservationIds;

    public RegisterReservationResponse(final List<Long> reservationIds) {
        this.reservationIds = reservationIds;
    }

    public static RegisterReservationResponse of(final List<Reservation> reservations) {
        List<Long> ids = reservations.stream()
                .map(Reservation::getId)
                .collect(Collectors.toList());
        return new RegisterReservationResponse(ids);
    }
}
