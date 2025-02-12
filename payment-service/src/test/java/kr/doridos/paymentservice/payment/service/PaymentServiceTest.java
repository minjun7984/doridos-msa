package kr.doridos.paymentservice.payment.service;

import kr.doridos.common.exception.ErrorCode;
import kr.doridos.paymentservice.payment.controller.PaymentClient;
import kr.doridos.paymentservice.payment.dto.PaymentCancelRequest;
import kr.doridos.paymentservice.payment.dto.PaymentCancelResponse;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmRequest;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmResponse;
import kr.doridos.paymentservice.payment.entity.Payment;
import kr.doridos.paymentservice.payment.entity.PaymentStatus;
import kr.doridos.paymentservice.payment.exception.PaymentAlreadyProcessedException;
import kr.doridos.paymentservice.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class PaymentServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제_확인_성공() {
        // Given
        Long reservationId = 1L;
        Long userId = 1L;
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest("payment1", 10000, "paymentKey");
        PaymentConfirmResponse paymentConfirmResponse = new PaymentConfirmResponse(
                "paymentKey", "payment1", "orderName", 1000, ZonedDateTime.now(), ZonedDateTime.now(), PaymentStatus.DONE);
        String idempotencyKey = "payment:confirm:" + paymentConfirmRequest.getOrderId();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(idempotencyKey, "success", 10, TimeUnit.SECONDS)).willReturn(true);
        given(paymentClient.confirmPayment(paymentConfirmRequest)).willReturn(paymentConfirmResponse);

        PaymentConfirmResponse result = paymentService.confirmPayment(paymentConfirmRequest, reservationId, userId);

        assertThat(result).isEqualTo(paymentConfirmResponse);
    }

    @Test
    void 결제_확인_실패_예외처리() {
        Long reservationId = 1L;
        Long userId = 1L;
        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest("payment1", 10000, "paymentKey");
        String idempotencyKey = "payment:confirm:" + paymentConfirmRequest.getOrderId();

        given(redisTemplate.opsForValue()).willReturn(valueOperations);
        given(valueOperations.setIfAbsent(idempotencyKey, "success", 10, TimeUnit.SECONDS)).willReturn(false); // 이미 존재한다고 가정

        assertThatThrownBy(() -> paymentService.confirmPayment(paymentConfirmRequest, reservationId, userId))
                .isInstanceOf(PaymentAlreadyProcessedException.class)
                .hasMessage(ErrorCode.PAYMENT_ALREADY_PROCESSED.getMessage());

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void 결제_취소_성공() {
        String paymentKey = "paymentKey";
        Payment payment = Payment.builder()
                .paymentKey(paymentKey)
                .build();
        PaymentCancelRequest request = new PaymentCancelRequest("단순 변심");
        PaymentCancelResponse response = new PaymentCancelResponse("paymentKey", "payment1", "orderName", PaymentStatus.CANCELED, ZonedDateTime.now(), ZonedDateTime.now());

        given(paymentRepository.findById(1L)).willReturn(Optional.of(payment));
        given(paymentClient.cancelPayment(paymentKey, request)).willReturn(response);

        PaymentCancelResponse result = paymentService.cancelPayment(request, 1L);

        assertThat(result).isEqualTo(response);
    }
}
