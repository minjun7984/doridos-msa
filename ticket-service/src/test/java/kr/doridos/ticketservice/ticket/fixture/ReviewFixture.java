package kr.doridos.ticketservice.ticket.fixture;

import kr.doridos.ticketservice.review.entity.Review;

import java.time.LocalDateTime;

public class ReviewFixture {

    public static Review 리뷰_생성() {
        return Review.builder()
                .id(1L)
                .ticketId(1L)
                .content("배우들의 환상적인 연기에 전율이 돋았습니다.")
                .rating(5)
                .createAt(LocalDateTime.now())
                .build();
    }

    public static Review 리뷰_생성2() {
        return Review.builder()
                .id(2L)
                .ticketId(1L)
                .content("무대의 음향이 너무나 훌륭하고 배우님들 각각의 연기력이 너무 뛰어나셔서 감동의 눈물이 났습니다.")
                .rating(4)
                .createAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
