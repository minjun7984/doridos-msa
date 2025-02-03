package kr.doridos.userservice.user.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class NicknameAlreadyExistsException extends BusinessException {
    public NicknameAlreadyExistsException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    public NicknameAlreadyExistsException(ErrorCode errorCode) {
        super(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }
}
