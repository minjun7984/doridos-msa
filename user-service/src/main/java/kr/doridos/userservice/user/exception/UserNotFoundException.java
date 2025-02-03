package kr.doridos.userservice.user.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(ErrorCode errorCode) {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
