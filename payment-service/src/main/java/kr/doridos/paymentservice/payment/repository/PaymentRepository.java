package kr.doridos.paymentservice.payment.repository;

import kr.doridos.paymentservice.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
