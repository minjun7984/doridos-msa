package kr.doridos.ticketservice.ticket.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class ForbiddenException extends BusinessException {

    public ForbiddenException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.FORBIDDEN_USER);
    }

    public ForbiddenException(ErrorCode errorCode) {
        super(ErrorCode.FORBIDDEN_USER);
    }
}
