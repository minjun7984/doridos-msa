package kr.doridos.reservationservice.reservation.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class SeatNotFoundException extends BusinessException {

    public SeatNotFoundException(ErrorCode errorCode) {
        super(ErrorCode.SEAT_NOT_FOUND);
    }
}
