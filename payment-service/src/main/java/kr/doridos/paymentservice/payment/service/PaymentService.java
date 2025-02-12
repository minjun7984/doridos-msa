package kr.doridos.paymentservice.payment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.doridos.common.exception.ErrorCode;
import kr.doridos.paymentservice.kafka.event.PaymentCompletedProducer;
import kr.doridos.paymentservice.payment.controller.PaymentClient;
import kr.doridos.paymentservice.payment.dto.PaymentCancelRequest;
import kr.doridos.paymentservice.payment.dto.PaymentCancelResponse;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmRequest;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmResponse;
import kr.doridos.paymentservice.payment.entity.Payment;
import kr.doridos.paymentservice.payment.exception.PaymentAlreadyProcessedException;
import kr.doridos.paymentservice.payment.exception.PaymentNotFoundException;
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
    private final StringRedisTemplate redisTemplate;
    private final PaymentCompletedProducer paymentCompletedProducer;

    public PaymentService(final PaymentClient paymentClient, final PaymentRepository paymentRepository, final StringRedisTemplate redisTemplate, final PaymentCompletedProducer paymentCompletedProducer) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.redisTemplate = redisTemplate;
        this.paymentCompletedProducer = paymentCompletedProducer;
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
        paymentCompletedProducer.paymentCompletedEvent(reservationId);
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




