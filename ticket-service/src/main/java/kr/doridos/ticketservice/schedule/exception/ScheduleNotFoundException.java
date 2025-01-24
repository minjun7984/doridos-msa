package kr.doridos.ticketservice.schedule.exception;

import kr.doridos.common.exception.BusinessException;
import kr.doridos.common.exception.ErrorCode;

public class ScheduleNotFoundException extends BusinessException {
    public ScheduleNotFoundException(String message, ErrorCode errorCode) {
        super(message, ErrorCode.SCHEDULE_NOT_FOUND);
    }

    public ScheduleNotFoundException(ErrorCode errorCode) {
        super(ErrorCode.SCHEDULE_NOT_FOUND);
    }
}

