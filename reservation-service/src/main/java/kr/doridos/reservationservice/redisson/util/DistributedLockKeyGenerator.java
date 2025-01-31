package kr.doridos.reservationservice.redisson.util;

import kr.doridos.reservationservice.reservation.dto.ReservationRequest;

import java.util.ArrayList;
import java.util.List;

public class DistributedLockKeyGenerator {
    public static List<Long> generateKeys(int[] paramIndexes, Object[] args, String key) {
        List<Long> seatIds = new ArrayList<>();
        for (int index : paramIndexes) {
            Object param = args[index];
            if (param instanceof ReservationRequest) {
                ReservationRequest request = (ReservationRequest) param;
                seatIds.addAll(request.getSeatIds()); // 좌석 ID 리스트 추출
            }
        }
        return seatIds;
    }
}

