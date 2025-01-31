package kr.doridos.reservationservice.reservation.repository;

import kr.doridos.reservationservice.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query(value = "SELECT EXISTS (" +
            "  SELECT 1 " +
            "  FROM reservation r " +
            "  WHERE r.ticket_id = :ticketId " +
            "  AND r.schedule_id = :scheduleId " +
            "  AND r.seat_id IN :seats" +
            ")", nativeQuery = true)
    boolean existsReservationBySeatsAndScheduleIdAndTicketId(@Param("scheduleId") Long scheduleId,
                                                             @Param("seats") List<Long> seats,
                                                             @Param("ticketId") Long ticketId);

    List<Reservation> findByUserId(@Param("userId") Long userId);
}
