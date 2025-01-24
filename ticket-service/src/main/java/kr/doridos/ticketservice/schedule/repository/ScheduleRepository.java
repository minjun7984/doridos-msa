package kr.doridos.ticketservice.schedule.repository;

import kr.doridos.ticketservice.schedule.entity.Schedule;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT COUNT(s) FROM Schedule s " + "WHERE s.startDate <= :endDate AND s.endDate >= :startDate " +
            "AND s.ticket = :ticket")
    int getSchedulesNumByStartTime(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate,
                                   @Param("ticket") Ticket ticket);

    List<Schedule> findAllByTicketId(Long ticketId);

}
