package kr.doridos.reservationservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatKafkaProducer {

    private static final String SEATS_RESERVATION_TOPIC = "seat-reservation-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void seatReservationEvent(final List<Long> seatIds) throws JsonProcessingException {
        SeatReservationMessage message = new SeatReservationMessage(seatIds);
        String seatsReservationMessage = objectMapper.writeValueAsString(message);

        kafkaTemplate.send(SEATS_RESERVATION_TOPIC, seatsReservationMessage);
        log.info("Kafka 메시지 전송 완료: {}", seatsReservationMessage);
    }
}
