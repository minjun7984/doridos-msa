package kr.doridos.reservationservice.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table
@Entity
@Builder
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long scheduleId;

    @Column(nullable = false)
    private Long ticketId;

    @Column(nullable = false)
    private String ticketTitle;

    @Column(nullable = false)
    private Long seatId;

    @Column(nullable = false)
    private String seatSection;

    @Column(nullable = false)
    private int seatNum;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;

    public void isBookedStatus() {
        this.reservationStatus = ReservationStatus.BOOKED;
    }

    public void isCanceledStatus() {
        this.reservationStatus = ReservationStatus.CANCELED;
    }
}
