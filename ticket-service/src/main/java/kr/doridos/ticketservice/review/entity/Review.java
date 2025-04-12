package kr.doridos.ticketservice.review.entity;

import jakarta.persistence.*;
import kr.doridos.common.auth.UserInfo;
import kr.doridos.ticketservice.review.dto.ReviewRequest;
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
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long ticketId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int rating;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updateAt;

    @Column(nullable = true)
    private LocalDateTime deleteAt;

    public Review(ReviewRequest request, Long ticketId, UserInfo userInfo) {
        this.userId = userInfo.getUserId();
        this.ticketId = ticketId;
        this.content = request.content();
        this.rating = request.rating();
    }
}
