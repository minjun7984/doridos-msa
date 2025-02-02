package kr.doridos.ticketservice.schedule.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.doridos.ticketservice.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ScheduleSeatKafkaConsumer {

    private final ScheduleService scheduleService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "seat-reservation-topic", groupId = "ticket-service-group")
    public void consumeScheduleSeatReservedEvent(final String seatReservationMessage) throws JsonProcessingException {
            log.info("Received raw message: {}", seatReservationMessage);

            SeatReservationMessage message = objectMapper.readValue(seatReservationMessage, SeatReservationMessage.class);
            scheduleService.updateSeatStatus(message.getSeatIds());
    }
}


