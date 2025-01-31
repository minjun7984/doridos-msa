package kr.doridos.reservationservice.reservation.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class ReservationNotCollectUserException extends BusinessException {
    public ReservationNotCollectUserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
