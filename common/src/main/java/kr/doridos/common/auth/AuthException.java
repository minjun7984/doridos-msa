package kr.doridos.common.auth;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class AuthException extends BusinessException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
