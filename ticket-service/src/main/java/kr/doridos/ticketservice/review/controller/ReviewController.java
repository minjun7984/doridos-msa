package kr.doridos.ticketservice.review.controller;

import kr.doridos.common.auth.AuthUser;
import kr.doridos.common.auth.UserInfo;
import kr.doridos.ticketservice.review.dto.ReviewRequest;
import kr.doridos.ticketservice.review.dto.ReviewResponse;
import kr.doridos.ticketservice.review.service.ReviewService;
import kr.doridos.ticketservice.review.util.ReviewSortType;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/tickets/{ticketId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(final ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<Long> createReview(@PathVariable final Long ticketId, @RequestBody final ReviewRequest request, @AuthUser final UserInfo userInfo) {
        Long reviewId = reviewService.createReview(request, ticketId, userInfo);
        return ResponseEntity.created(URI.create(String.format("/tickets/%d/reviews/%d", ticketId, reviewId))).build();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResponse>> getReviewsByTicket(
            @PathVariable final Long ticketId,
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            @RequestParam(defaultValue = "latest") final String sortType) {
        return ResponseEntity.ok(reviewService.getReviewsByTicket(ticketId, page, size, sortType));
    }
}
