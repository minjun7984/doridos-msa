package kr.doridos.userservice.auth.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class SignInFailureException extends BusinessException {

    public SignInFailureException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.SIGN_IN_FAIL);
    }

    public SignInFailureException(ErrorCode errorCode) {
        super(ErrorCode.SIGN_IN_FAIL);
    }
}
