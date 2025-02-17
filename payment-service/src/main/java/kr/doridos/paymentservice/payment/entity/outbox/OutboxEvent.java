package kr.doridos.paymentservice.payment.entity.outbox;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long aggregateId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentEventType eventType;

    @Column(nullable = false)
    private String payload;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EventStatus status;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @Builder
    public OutboxEvent(Long aggregateId, PaymentEventType eventType, String payload) {
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createAt = LocalDateTime.now();
        this.status = EventStatus.PENDING;
    }

    public void updateStatus() {
        this.status = EventStatus.PROCESSED;
    }
}
