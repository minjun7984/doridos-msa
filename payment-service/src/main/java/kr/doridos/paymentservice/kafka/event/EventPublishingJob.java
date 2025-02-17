package kr.doridos.paymentservice.kafka.event;

import kr.doridos.paymentservice.payment.entity.outbox.EventStatus;
import kr.doridos.paymentservice.payment.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublishingJob {

    private static final String PAYMENT_COMPLETED_TOPIC = "payment-completed-topic";

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 주기적으로 Outbox 테이블을 조회하여 상태가 PENDING 이벤트를 발행
     */
    @Scheduled(fixedDelay = 1000) //1초마다 실행
    public void publishOutboxEvents() {
        outboxRepository.findByStatus(EventStatus.PENDING)
                .forEach(event -> {
                    try {
                        kafkaTemplate.send(PAYMENT_COMPLETED_TOPIC, event.getPayload());
                        event.updateStatus();
                        outboxRepository.save(event);
                        log.info("Kafka 메시지 전송 완료: {}", event.getPayload());
                    } catch (Exception e) {
                        log.error("Kafka 메시지 전송 실패: {}", e.getMessage(), e);
                    }
                });
    }
}
