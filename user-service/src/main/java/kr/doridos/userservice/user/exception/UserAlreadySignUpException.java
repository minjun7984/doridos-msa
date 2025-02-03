package kr.doridos.userservice.user.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class UserAlreadySignUpException extends BusinessException {
    public UserAlreadySignUpException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.USER_ALREADY_SIGNUP);
    }

    public UserAlreadySignUpException(ErrorCode errorCode) {
        super(ErrorCode.USER_ALREADY_SIGNUP);
    }
}
