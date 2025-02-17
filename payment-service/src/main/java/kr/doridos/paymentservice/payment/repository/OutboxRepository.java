package kr.doridos.paymentservice.payment.repository;

import kr.doridos.paymentservice.payment.entity.outbox.EventStatus;
import kr.doridos.paymentservice.payment.entity.outbox.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByStatus(EventStatus status);

}
