package kr.doridos.ticketservice.review.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class InvalidReviewContentException extends BusinessException {

    public InvalidReviewContentException(ErrorCode errorCode) {
        super(errorCode);
    }
}
