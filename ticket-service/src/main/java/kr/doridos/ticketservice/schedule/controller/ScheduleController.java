package kr.doridos.ticketservice.schedule.controller;

import kr.doridos.common.auth.AuthUser;
import kr.doridos.common.auth.UserInfo;
import kr.doridos.ticketservice.schedule.dto.ScheduleCreateRequest;
import kr.doridos.ticketservice.schedule.dto.ScheduleResponse;
import kr.doridos.ticketservice.schedule.dto.ScheduleSeatResponse;
import kr.doridos.ticketservice.schedule.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(final ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @PostMapping("/schedules")
    public ResponseEntity<Void> createSchedule(@RequestBody final ScheduleCreateRequest request,
                                               @AuthUser final UserInfo userInfo) {
        final Long scheduleId = scheduleService.createScheduleWithSeats(request, userInfo);
        return ResponseEntity.created(URI.create("/schedules" + scheduleId)).build();
    }

    @GetMapping("tickets/{ticketId}/schedules")
    public ResponseEntity<List<ScheduleResponse>> findAllSchedules(@PathVariable("ticketId") final Long ticketId) {
        final List<ScheduleResponse> schedules = scheduleService.findAllSchedules(ticketId);
        return ResponseEntity.ok(schedules);
    }

    @GetMapping("/tickets/{ticketId}/schedules/{scheduleId}/seats")
    public ResponseEntity<List<ScheduleSeatResponse>> findAllScheduleSeats(@PathVariable("ticketId") final Long ticketId,
                                                                           @PathVariable("scheduleId") final Long scheduleId) {
        final List<ScheduleSeatResponse> scheduleSeatResponses = scheduleService.findAllScheduleSeats(scheduleId);
        return ResponseEntity.ok(scheduleSeatResponses);
    }
}
