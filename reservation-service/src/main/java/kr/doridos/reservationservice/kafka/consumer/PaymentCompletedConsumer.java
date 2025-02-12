package kr.doridos.reservationservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.doridos.reservationservice.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedConsumer {

    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-completed-topic", groupId = "reservation-service")
    public void consumePaymentCompletedEvent(final String reservationId) throws JsonProcessingException {
        log.info("Receive message: {}", reservationId);

        PaymentCompletedMessage message = objectMapper.readValue(reservationId, PaymentCompletedMessage.class);
        reservationService.updateReservationStatusIsBooked(message.getReservationId());
    }
}
