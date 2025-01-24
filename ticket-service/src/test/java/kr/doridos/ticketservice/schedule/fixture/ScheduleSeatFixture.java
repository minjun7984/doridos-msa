package kr.doridos.ticketservice.schedule.fixture;

import kr.doridos.ticketservice.place.entity.Section;
import kr.doridos.ticketservice.schedule.entity.ScheduleSeat;

public class ScheduleSeatFixture {

    public static ScheduleSeat 좌석생성() {
        return ScheduleSeat.builder()
                .id(1L)
                .section(Section.A)
                .seatNum(1)
                .price(100000)
                .isReserved(false)
                .schedule(ScheduleFixture.스케줄_생성())
                .build();
    }

    public static ScheduleSeat 좌석생성2() {
        return ScheduleSeat.builder()
                .id(2L)
                .section(Section.B)
                .seatNum(1)
                .price(100000)
                .isReserved(false)
                .schedule(ScheduleFixture.스케줄_생성())
                .build();
    }

    public static ScheduleSeat 예약된_좌석생성() {
        return ScheduleSeat.builder()
                .id(3L)
                .isReserved(true)
                .schedule(ScheduleFixture.스케줄_생성())
                .build();
    }
}
