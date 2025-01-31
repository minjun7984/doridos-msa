package kr.doridos.reservationservice.reservation.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import kr.doridos.reservationservice.reservation.client.response.TicketInfoFeignResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@CircuitBreaker(name = "reservation-service-circuit-breaker")
@FeignClient(name = "ticket-service", path = "/tickets/details")
public interface TicketFeignClient {

    @GetMapping
    List<TicketInfoFeignResponse> getReservationsWithSeatsByTicketAndSchedule(@RequestParam("ticketId") final Long ticketId, @RequestParam("scheduleId") final Long scheduleId, @RequestParam("seatIds") final List<Long> seatIds);

}
