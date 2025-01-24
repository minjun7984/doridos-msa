package kr.doridos.ticketservice.schedule.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class ReservationNotStartException extends BusinessException {

    public ReservationNotStartException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.RESERVATION_NOT_START);
    }

    public ReservationNotStartException(ErrorCode errorCode) {
        super(ErrorCode.RESERVATION_NOT_START);
    }
}
