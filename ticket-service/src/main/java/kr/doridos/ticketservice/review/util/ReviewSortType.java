package kr.doridos.ticketservice.review.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

@Getter
@RequiredArgsConstructor
public enum ReviewSortType {
    RATING_DESC("rating_desc"),
    RATING_ASC("rating_asc"),
    LATEST("latest");

    private final String type;
    public Sort toSort() {
        return switch (this) {
            case RATING_DESC -> Sort.by(Sort.Direction.DESC, "rating");
            case RATING_ASC -> Sort.by(Sort.Direction.ASC, "rating");
            case LATEST -> Sort.by(Sort.Direction.DESC, "createAt");
        };
    }
}