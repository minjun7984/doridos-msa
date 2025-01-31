package kr.doridos.reservationservice.reservation.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class SeatAlreadyReservedException extends BusinessException {
    public SeatAlreadyReservedException(ErrorCode errorCode) {
        super(ErrorCode.SEAT_ALREADY_RESERVED);
    }
}
