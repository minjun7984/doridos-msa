package kr.doridos.paymentservice.kafka.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.doridos.paymentservice.kafka.PaymentCompletedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCompletedProducer {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment-completed-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void paymentCompletedEvent(final Long reservationId) throws JsonProcessingException {
        PaymentCompletedMessage message = new PaymentCompletedMessage(reservationId);
        String paymentCompletedMessage = objectMapper.writeValueAsString(message);

        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, paymentCompletedMessage);
        log.info("Kafka 메시지 전송 완료: {}", paymentCompletedMessage);
    }
}

