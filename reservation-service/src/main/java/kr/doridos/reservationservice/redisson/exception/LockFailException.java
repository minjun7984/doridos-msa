package kr.doridos.reservationservice.redisson.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class LockFailException extends BusinessException {

    public LockFailException(ErrorCode errorCode) {
        super(ErrorCode.LOCK_ACQUISITION_FAILED);
    }
}
