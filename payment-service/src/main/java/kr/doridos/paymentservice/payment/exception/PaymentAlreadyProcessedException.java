package kr.doridos.paymentservice.payment.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class PaymentAlreadyProcessedException extends BusinessException {

    public PaymentAlreadyProcessedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public PaymentAlreadyProcessedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
