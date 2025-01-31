package kr.doridos.reservationservice.reservation.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class ReservationNotFoundException extends BusinessException {
    public ReservationNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
