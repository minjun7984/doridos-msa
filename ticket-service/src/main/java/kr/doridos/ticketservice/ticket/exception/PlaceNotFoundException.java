package kr.doridos.ticketservice.ticket.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class PlaceNotFoundException extends BusinessException {

    public PlaceNotFoundException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.PLACE_NOT_FOUND);
    }

    public PlaceNotFoundException(ErrorCode errorCode) {
        super(ErrorCode.PLACE_NOT_FOUND);
    }
}
