package kr.doridos.paymentservice.payment.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class PaymentNotFoundException extends BusinessException {
    public PaymentNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public PaymentNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
