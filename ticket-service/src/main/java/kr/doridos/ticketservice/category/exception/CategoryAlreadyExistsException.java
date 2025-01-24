package kr.doridos.ticketservice.category.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class CategoryAlreadyExistsException extends BusinessException {
    public CategoryAlreadyExistsException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.CATEGORY_EXIST);
    }

    public CategoryAlreadyExistsException(ErrorCode errorCode) {
        super(ErrorCode.CATEGORY_EXIST);
    }
}
