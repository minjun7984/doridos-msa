package kr.doridos.ticketservice.review.service;

import kr.doridos.common.auth.UserInfo;
import kr.doridos.common.exception.ErrorCode;
import kr.doridos.ticketservice.review.dto.ReviewRequest;
import kr.doridos.ticketservice.review.dto.ReviewResponse;
import kr.doridos.ticketservice.review.entity.Review;
import kr.doridos.ticketservice.review.exception.InvalidReviewContentException;
import kr.doridos.ticketservice.review.exception.ReviewNotFoundException;
import kr.doridos.ticketservice.review.repository.ReviewRepository;
import kr.doridos.ticketservice.review.util.ReviewSortType;
import kr.doridos.ticketservice.ticket.entity.Ticket;
import kr.doridos.ticketservice.ticket.exception.TicketNotFoundException;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {

    private final TicketRepository ticketRepository;
    private final ReviewRepository reviewRepository;

    public ReviewService(final TicketRepository ticketRepository, final ReviewRepository reviewRepository) {
        this.ticketRepository = ticketRepository;
        this.reviewRepository = reviewRepository;
    }

    public Long createReview(final ReviewRequest reviewRequest, final Long ticketId, final UserInfo userInfo) {
        validateReviewContent(reviewRequest.content());

        final Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ErrorCode.TICKET_NOT_FOUND));
        final Review savedReview = reviewRepository.save(new Review(reviewRequest, ticketId, userInfo));

        return savedReview.getId();
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReview(final Long reviewId) {
        final Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(ErrorCode.REVIEW_NOT_FOUND));

        return ReviewResponse.from(review);
    }

    @Transactional(readOnly = true)
    public Page<ReviewResponse> getReviewsByTicket(final Long ticketId, final int page, final int size, final String sortType) {
        ReviewSortType reviewSortType = ReviewSortType.valueOf(sortType.toUpperCase());
        final Pageable pageable = PageRequest.of(page, size, reviewSortType.toSort());

        return reviewRepository.findAllByTicketId(ticketId, pageable)
                .map(ReviewResponse::from);
    }

    private void validateReviewContent(String content) {
        if (content == null || content.length() < 20) {
            throw new InvalidReviewContentException(ErrorCode.REVIEW_CONTENT_TOO_SHORT);
        }
    }
}
