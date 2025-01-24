package kr.doridos.ticketservice.schedule.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class DuplicateScheduleTimeException extends BusinessException {

    public DuplicateScheduleTimeException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.SCHEDULE_ALREADY_EXIST);
    }

    public DuplicateScheduleTimeException(ErrorCode errorCode) {
        super(ErrorCode.SCHEDULE_ALREADY_EXIST);
    }
}
