package kr.doridos.reservationservice.kafka.producer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeatReservationMessage {

    private List<Long> seatIds;

}
