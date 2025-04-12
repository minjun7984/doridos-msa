package kr.doridos.ticketservice.review.dto;

import kr.doridos.ticketservice.review.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        String content,
        int rating,
        LocalDateTime createAt
) {
    public static ReviewResponse from(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getCreateAt()
        );
    }
}