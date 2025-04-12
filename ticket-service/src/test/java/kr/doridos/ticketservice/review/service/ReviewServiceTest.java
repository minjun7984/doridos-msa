package kr.doridos.ticketservice.review.service;

import kr.doridos.common.auth.UserInfo;
import kr.doridos.ticketservice.review.dto.ReviewRequest;
import kr.doridos.ticketservice.review.dto.ReviewResponse;
import kr.doridos.ticketservice.review.entity.Review;
import kr.doridos.ticketservice.review.exception.InvalidReviewContentException;
import kr.doridos.ticketservice.ticket.fixture.ReviewFixture;
import kr.doridos.ticketservice.review.repository.ReviewRepository;
import kr.doridos.ticketservice.review.util.ReviewSortType;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import kr.doridos.ticketservice.ticket.exception.TicketNotFoundException;
import kr.doridos.ticketservice.ticket.fixture.TicketFixture;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;
import kr.doridos.ticketservice.util.UnitTestSupport;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class ReviewServiceTest extends UnitTestSupport {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @DisplayName("리뷰 생성 테스트")
    @Nested
    class CreateReviewTest {

        @Test
        void 리뷰_생성에_성공한다() {
            // given
            ReviewRequest reviewRequest = new ReviewRequest("배우들의 환상적인 연기에 전율이 돋았습니다.", 5);
            Long ticketId = 1L;
            UserInfo userInfo = new UserInfo(1L, "USER");

            Review mockReview = Review.builder()
                    .id(1L)
                    .ticketId(ticketId)
                    .userId(userInfo.getUserId())
                    .content(reviewRequest.content())
                    .rating(reviewRequest.rating())
                    .build();
            Ticket mockTicket = TicketFixture.티켓_생성();

            given(ticketRepository.findById(ticketId)).willReturn(Optional.of(mockTicket));
            given(reviewRepository.save(any(Review.class))).willReturn(mockReview);

            Long createdId = reviewService.createReview(reviewRequest, ticketId, userInfo);

            assertThat(createdId).isEqualTo(1L);
            verify(reviewRepository).save(any(Review.class));
        }

        @Test
        void 티켓이_존재하지_않으면_리뷰생성에_실패한다() {
            ReviewRequest reviewRequest = new ReviewRequest("배우들의 환상적인 연기에 전율이 돋았습니다.", 5);
            Long ticketId = 1L;
            UserInfo userInfo = new UserInfo(1L, "USER");

            given(ticketRepository.findById(ticketId)).willReturn(Optional.empty());

            assertThatThrownBy(() -> reviewService.createReview(reviewRequest, ticketId, userInfo))
                    .isInstanceOf(TicketNotFoundException.class)
                    .hasMessage("티켓을 찾을 수 없습니다.");

        }

        @ParameterizedTest
        @ValueSource(strings = {
                "리뷰",
                "",
                "테스트중인데 이건 열아홉자입니다요."
        })
        void 리뷰의_길이가_20자_이하면_리뷰생성에_실패한다(String content) {
            ReviewRequest reviewRequest = new ReviewRequest(content, 5);
            UserInfo userInfo = new UserInfo(1L, "USER");

            assertThatThrownBy(() -> reviewService.createReview(reviewRequest, 1L, userInfo))
                    .isInstanceOf(InvalidReviewContentException.class)
                    .hasMessage("리뷰는 20자 이상 작성해야합니다.");
        }
    }

    @DisplayName("리뷰 조회 테스트")
    @Nested
    class GetReviewTest {

        @Test
        void 리뷰_단건_조회에_성공한다() {
            Review review = ReviewFixture.리뷰_생성();

            given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

            ReviewResponse response = reviewService.getReview(review.getId());

            assertSoftly(softly -> {
                softly.assertThat(review.getContent()).isEqualTo(response.content());
                softly.assertThat(review.getRating()).isEqualTo(response.rating());
                softly.assertThat(review.getCreateAt()).isEqualTo(response.createAt());
            });
        }

        @Test
        void 최신순으로_리뷰를_페이징_조회한다() {
            Long ticketId = 1L;
            String sort = "latest";
            ReviewSortType reviewSortType = ReviewSortType.valueOf(sort.toUpperCase());
            Review recentReview = ReviewFixture.리뷰_생성();
            Review oldReview = ReviewFixture.리뷰_생성2();

            Pageable pageable = PageRequest.of(0, 10, reviewSortType.toSort());

            List<Review> reviews = List.of(recentReview, oldReview);
            Page<Review> mockPage = new PageImpl<>(reviews, pageable, 2);

            given(reviewRepository.findAllByTicketId(ticketId, pageable)).willReturn(mockPage);

            Page<ReviewResponse> result = reviewService.getReviewsByTicket(ticketId, 0, 10, sort);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).content()).isEqualTo(recentReview.getContent());
            assertThat(result.getContent().get(1).content()).isEqualTo(oldReview.getContent());
            verify(reviewRepository).findAllByTicketId(ticketId, pageable);
        }

        @Test
        void 평점이_높은순으로_리뷰를_페이징_조회한다() {
            Long ticketId = 1L;
            String sort = "rating_desc";
            ReviewSortType reviewSortType = ReviewSortType.valueOf(sort.toUpperCase());
            Review highRatedReview = ReviewFixture.리뷰_생성();
            Review lowRatedReview = ReviewFixture.리뷰_생성2();

            Pageable pageable = PageRequest.of(0, 10, reviewSortType.toSort());

            List<Review> reviews = List.of(highRatedReview, lowRatedReview);
            Page<Review> mockPage = new PageImpl<>(reviews, pageable, 2);

            given(reviewRepository.findAllByTicketId(ticketId, pageable)).willReturn(mockPage);

            Page<ReviewResponse> result = reviewService.getReviewsByTicket(ticketId, 0, 10, sort);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).rating()).isEqualTo(highRatedReview.getRating());
            assertThat(result.getContent().get(1).rating()).isEqualTo(lowRatedReview.getRating());
        }

        @Test
        void 평점이_낮은순으로_리뷰를_페이징_조회한다() {
            Long ticketId = 1L;
            String sort = "rating_asc";
            ReviewSortType reviewSortType = ReviewSortType.valueOf(sort.toUpperCase());
            Review highRatedReview = ReviewFixture.리뷰_생성();
            Review lowRatedReview = ReviewFixture.리뷰_생성2();

            Pageable pageable = PageRequest.of(0, 10, reviewSortType.toSort());

            List<Review> reviews = List.of(lowRatedReview, highRatedReview);
            Page<Review> mockPage = new PageImpl<>(reviews, pageable, 2);

            given(reviewRepository.findAllByTicketId(ticketId, pageable)).willReturn(mockPage);

            Page<ReviewResponse> result = reviewService.getReviewsByTicket(ticketId, 0, 10, sort);

            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).rating()).isEqualTo(lowRatedReview.getRating());
            assertThat(result.getContent().get(1).rating()).isEqualTo(highRatedReview.getRating());
        }

        @Test
        void 리뷰가_없으면_빈_페이지를_반환한다() {
            Long ticketId = 99L;
            String sort = "rating_asc";
            ReviewSortType reviewSortType = ReviewSortType.valueOf(sort.toUpperCase());
            Pageable pageable = PageRequest.of(0, 10, reviewSortType.toSort());
            Page<Review> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(reviewRepository.findAllByTicketId(ticketId, pageable)).willReturn(emptyPage);

            Page<ReviewResponse> result = reviewService.getReviewsByTicket(ticketId, 0, 10, sort);

            assertThat(result.getContent()).isEmpty();
        }
    }

}


