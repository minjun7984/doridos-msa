package kr.doridos.reservationservice.redisson.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class LockInterruptedException extends BusinessException {

    public LockInterruptedException(ErrorCode errorCode) {
        super(ErrorCode.LOCK_INTERRUPTED);
    }
}
