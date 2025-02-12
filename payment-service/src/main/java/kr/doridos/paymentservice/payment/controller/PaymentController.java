package kr.doridos.paymentservice.payment.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kr.doridos.common.auth.AuthUser;
import kr.doridos.common.auth.UserInfo;
import kr.doridos.paymentservice.payment.dto.PaymentCancelRequest;
import kr.doridos.paymentservice.payment.dto.PaymentCancelResponse;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmRequest;
import kr.doridos.paymentservice.payment.dto.PaymentConfirmResponse;
import kr.doridos.paymentservice.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/{reservationId}/payment")
    public ResponseEntity<PaymentConfirmResponse> confirm(@RequestBody final PaymentConfirmRequest paymentConfirmRequest,
                                                          @PathVariable("reservationId") final Long reservationId,
                                                          @AuthUser UserInfo userInfo) throws JsonProcessingException {
        final PaymentConfirmResponse paymentConfirmResponse = paymentService.confirmPayment(paymentConfirmRequest, reservationId, userInfo.getUserId());
        return ResponseEntity.ok(paymentConfirmResponse);
    }

    @PostMapping("/api/payment/{paymentId}/cancel")
    public ResponseEntity<PaymentCancelResponse> cancel(@RequestBody final PaymentCancelRequest request,
                                                        @PathVariable("paymentId") final Long paymentId,
                                                        @AuthUser UserInfo userInfo) {
        final PaymentCancelResponse paymentCancelResponse = paymentService.cancelPayment(request, paymentId);
        return ResponseEntity.ok(paymentCancelResponse);
    }
}
