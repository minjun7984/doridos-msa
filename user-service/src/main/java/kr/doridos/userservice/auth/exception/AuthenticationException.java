package kr.doridos.userservice.auth.exception;


import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public AuthenticationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
