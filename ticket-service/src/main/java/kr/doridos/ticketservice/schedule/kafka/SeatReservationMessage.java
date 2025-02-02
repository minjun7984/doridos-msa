package kr.doridos.ticketservice.schedule.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SeatReservationMessage {

    private List<Long> seatIds;

}
