package kr.doridos.reservationservice.reservation.exception.handler;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CircuitBreakerHandler {

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<?> handleCallNotPermittedException(CallNotPermittedException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("현재 서비스가 불가능합니다. 잠시 후 다시 시도해주세요.");
    }
}
