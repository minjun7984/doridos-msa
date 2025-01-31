package kr.doridos.reservationservice.reservation.controller;

import kr.doridos.common.auth.AuthUser;
import kr.doridos.common.auth.UserInfo;
import kr.doridos.reservationservice.reservation.dto.RegisterReservationResponse;
import kr.doridos.reservationservice.reservation.dto.ReservationRequest;
import kr.doridos.reservationservice.reservation.dto.ReservationResponse;
import kr.doridos.reservationservice.reservation.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReservationController {

    final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<RegisterReservationResponse> registerReservation(@AuthUser final UserInfo userInfo,
                                                                           @RequestBody final ReservationRequest request) {
        return ResponseEntity.ok(reservationService.registerReservation(userInfo.getUserId(), request));
    }

    @GetMapping("/reservations/me")
    public ResponseEntity<List<ReservationResponse>> findUserReservations(@AuthUser final UserInfo userInfo) {
        return ResponseEntity.ok(reservationService.getUserReservations(userInfo.getUserId()));
    }
}
