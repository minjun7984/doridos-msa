package kr.doridos.ticketservice.schedule.repository;

import kr.doridos.ticketservice.schedule.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {

    @EntityGraph(attributePaths = "schedule")
    List<ScheduleSeat> findAllByScheduleId(Long scheduleId);

    List<ScheduleSeat> findByIdIn(List<Long> seatIds);


}
