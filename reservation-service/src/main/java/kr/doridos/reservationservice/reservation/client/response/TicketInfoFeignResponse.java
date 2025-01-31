package kr.doridos.reservationservice.reservation.client.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class TicketInfoFeignResponse {

    private String ticketTitle;
    private String seatSection;
    private int seatNum;
    private Long seatId;
    private LocalDateTime startDate;

    public TicketInfoFeignResponse(String ticketTitle, String seatSection, int seatNum, Long seatId, LocalDateTime startDate) {
        this.ticketTitle = ticketTitle;
        this.seatSection = seatSection;
        this.seatNum = seatNum;
        this. seatId = seatId;
        this.startDate = startDate;
    }

}
