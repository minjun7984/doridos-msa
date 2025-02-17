package kr.doridos.paymentservice.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.doridos.common.exception.ErrorCode;
import kr.doridos.paymentservice.kafka.PaymentCompletedMessage;
import kr.doridos.paymentservice.payment.controller.PaymentClient;
import kr.doridos.paymentservice.payment.dto.PaymentCancelRequest;
import kr.doridos.paymentservice.payment.dto.PaymentCancelResponse;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmRequest;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmResponse;
import kr.doridos.paymentservice.payment.entity.outbox.OutboxEvent;
import kr.doridos.paymentservice.payment.entity.Payment;
import kr.doridos.paymentservice.payment.entity.outbox.PaymentEventType;
import kr.doridos.paymentservice.payment.exception.PaymentAlreadyProcessedException;
import kr.doridos.paymentservice.payment.exception.PaymentNotFoundException;
import kr.doridos.paymentservice.payment.repository.OutboxRepository;
import kr.doridos.paymentservice.payment.repository.PaymentRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class PaymentService {
    private static final String IDEMPOTENCY_KEY_PREFIX = "payment:confirm:";

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository, final OutboxRepository outboxRepository, final StringRedisTemplate redisTemplate, final ObjectMapper objectMapper) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public PaymentConfirmResponse confirmPayment(final PaymentConfirmRequest paymentConfirmRequest, final Long reservationId, final Long userId) throws JsonProcessingException {
        final String idempotencyKey = IDEMPOTENCY_KEY_PREFIX + paymentConfirmRequest.getOrderId();
        final Boolean isFirstRequest = redisTemplate.opsForValue().setIfAbsent(idempotencyKey, "success", 10, TimeUnit.SECONDS);

        if (!Boolean.TRUE.equals(isFirstRequest)) {
            throw new PaymentAlreadyProcessedException(ErrorCode.PAYMENT_ALREADY_PROCESSED);
        }

        final PaymentConfirmResponse paymentConfirmResponse = paymentClient.confirmPayment(paymentConfirmRequest);
        final Payment payment = paymentConfirmResponse.toPayment(reservationId, userId);
        paymentRepository.save(payment);

        OutboxEvent outboxEvent = OutboxEvent.builder()
                .aggregateId(payment.getId())
                .eventType(PaymentEventType.PAYMENT_COMPLETED)
                .payload(objectMapper.writeValueAsString(new PaymentCompletedMessage(reservationId)))
                .build();
        outboxRepository.save(outboxEvent);

        return paymentConfirmResponse;
    }

    public PaymentCancelResponse cancelPayment(final PaymentCancelRequest request, final Long paymentId) {
        final Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(ErrorCode.PAYMENT_NOT_FOUND));

        final PaymentCancelResponse response = paymentClient.cancelPayment(payment.getPaymentKey(), request);
        payment.changeStatus(response.getPaymentStatus(), response.getRequestedAt(), response.getApprovedAt());

        paymentRepository.save(payment);
        return response;
    }
}




