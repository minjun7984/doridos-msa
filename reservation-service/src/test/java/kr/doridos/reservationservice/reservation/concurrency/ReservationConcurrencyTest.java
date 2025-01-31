package kr.doridos.reservationservice.reservation.concurrency;

import kr.doridos.reservationservice.reservation.dto.ReservationRequest;
import kr.doridos.reservationservice.reservation.entity.Reservation;
import kr.doridos.reservationservice.reservation.repository.ReservationRepository;
import kr.doridos.reservationservice.reservation.service.ReservationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.util.List;
import java.util.concurrent.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@ActiveProfiles("test")
public class ReservationConcurrencyTest {

    @Autowired
    ReservationService reservationService;

    @Autowired
    ReservationRepository reservationRepository;

    @Test
    void 티켓예매시_좌석_동시성_테스트를_진행한다() throws InterruptedException {
        List<Long> seatIds = List.of(1L);
        ReservationRequest request = new ReservationRequest(1L, 1L, seatIds);

        int threadCount = 10;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    reservationService.registerReservation(1L, request);
                }  catch (Exception e) {
                    e.printStackTrace();
                } finally{
                    latch.countDown();
                }
            });
        }
        executorService.shutdown();
        latch.await();

        List<Reservation> reservations = reservationRepository.findAll();
        Assertions.assertThat(reservations.size()).isEqualTo(1);
    }

    @Test
    void 두_사용자가_좌석을_다르게_선택할_때_중복_예약이_발생하지_않는다() throws InterruptedException {
        ReservationRequest userARequest = new ReservationRequest(1L, 1L, List.of(1L));
        ReservationRequest userBRequest = new ReservationRequest(1L, 1L, List.of(1L, 2L));

        ExecutorService executorService = Executors.newFixedThreadPool(10);  // 스레드 수를 4로 설정
        CountDownLatch latch = new CountDownLatch(10);  // 4개의 스레드가 모두 완료될 때까지 대기

        // A 유저 스레드 (4개 스레드 생성)
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    reservationService.registerReservation(1L, userARequest);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });

            executorService.submit(() -> {
                try {
                    reservationService.registerReservation(2L, userBRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        executorService.shutdown();
        latch.await();

        List<Reservation> reservations = reservationRepository.findAll();
        Assertions.assertThat(reservations.size()).isEqualTo(1);
    }
}
