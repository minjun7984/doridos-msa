package kr.doridos.userservice.auth.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class OAuthNotSupportException extends BusinessException {

    public OAuthNotSupportException() {
        super(ErrorCode.NOT_SUPPORT_OAUTH_CLIENT);
    }

    public OAuthNotSupportException(ErrorCode errorCode) {
        super(ErrorCode.NOT_SUPPORT_OAUTH_CLIENT);
    }
}
