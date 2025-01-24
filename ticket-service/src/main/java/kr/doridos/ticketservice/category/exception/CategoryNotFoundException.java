package kr.doridos.ticketservice.category.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class CategoryNotFoundException extends BusinessException {

    public CategoryNotFoundException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.CATEGORY_NOT_FOUND);
    }

    public CategoryNotFoundException(ErrorCode errorCode) {
        super(ErrorCode.CATEGORY_NOT_FOUND);
    }
}
