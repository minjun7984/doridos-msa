package kr.doridos.ticketservice.review.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class ReviewNotFoundException extends BusinessException {

    public ReviewNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
