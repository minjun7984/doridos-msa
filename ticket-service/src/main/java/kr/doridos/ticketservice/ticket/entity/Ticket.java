package kr.doridos.ticketservice.ticket.entity;

import jakarta.persistence.*;
import kr.doridos.ticketservice.category.entity.Category;
import kr.doridos.ticketservice.place.entity.Place;
import kr.doridos.ticketservice.ticket.dto.TicketUpdateRequest;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Builder
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String runningTime;

    @Column(nullable = false)
    private LocalDateTime openDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userType;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;

    @Column(nullable = true)
    private LocalDateTime deleteAt;

    public Ticket(final String title,
                  final String content,
                  final String runningTime,
                  final LocalDateTime openDate,
                  final LocalDateTime endDate,
                  final LocalDateTime startDate,
                  final Place place,
                  final Long userId,
                  final String userType,
                  final Category category) {
        this.title = title;
        this.content = content;
        this.runningTime = runningTime;
        this.openDate = openDate;
        this.endDate = endDate;
        this.startDate = startDate;
        this.place = place;
        this.userId = userId;
        this.userType = userType;
        this.category = category;
    }

    @Builder
    public Ticket(final String title, final String content, final String runningTime, final LocalDateTime openDate,
                  final LocalDateTime endDate, final LocalDateTime startDate) {
        this.title = title;
        this.content = content;
        this.runningTime = runningTime;
        this.openDate = openDate;
        this.endDate = endDate;
        this.startDate = startDate;
    }

    public void update(final TicketUpdateRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.runningTime = request.getRunningTime();
        this.openDate = request.getOpenDate();
        this.endDate = request.getEndDate();
        this.startDate = request.getStartDate();
    }
}
