package kr.doridos.userservice.auth.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public InvalidTokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
