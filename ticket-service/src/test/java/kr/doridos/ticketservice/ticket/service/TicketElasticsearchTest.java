package kr.doridos.ticketservice.ticket.service;

import kr.doridos.ticketservice.ticket.dto.SearchAutoCompleteResponse;
import kr.doridos.ticketservice.ticket.entity.TicketDocument;
import kr.doridos.ticketservice.ticket.repository.TicketElasticsearchRepository;
import kr.doridos.ticketservice.util.ElasticSearchTestContainer;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import({ElasticSearchTestContainer.class})
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@TestPropertySource(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.config.enabled=false"
})
public class TicketElasticsearchTest {

    @Autowired
    private TicketSearchService ticketSearchService;

    @Autowired
    private TicketElasticsearchRepository elasticsearchRepository;

    @Test
    void 타이틀이_일치하면_티켓을_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "베토벤",
                "베토벤의 연주",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "뮤지컬");
        elasticsearchRepository.save(ticket);

        Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter("베토벤", null, null, null, 0, 10);

        assertThat(result.getContent().get(0).getTitle()).isEqualTo("베토벤");
        assertThat(result.getContent().get(0).getContent()).isEqualTo("베토벤의 연주");
    }

    @Test
    void 타이틀이_일치하지_않으면_빈페이지를_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "리버풀 vs 레알마드리드",
                "축구 경기가",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "수원경기장",
                "스포츠");
        elasticsearchRepository.save(ticket);

        Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter("바르셀로나", null, null, null, 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void 타이틀의_일부가_검색어와_일치하면_티켓을_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "웃는 남자",
                "웃는남자입니다",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "뮤지컬");
        elasticsearchRepository.save(ticket);

        Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter("남자", null, null, null, 0, 10);

        assertThat(result).isNotEmpty();
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("웃는 남자");
    }

    @Test
    void 타이틀과_카테고리가_모두_일치하지_않으면_빈_페이지를_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "모차르트",
                "모차르트의 연주",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "뮤지컬");
        elasticsearchRepository.save(ticket);

        Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter("차르트", "연극", null, null, 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void 기간안에_존재하는_티켓을_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "모차르트",
                "모차르트의 연주",
                "120분",
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().minusDays(2),
                "세종문화회관",
                "뮤지컬");
        elasticsearchRepository.save(ticket);

        LocalDateTime startDate = LocalDateTime.now().minusDays(4);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter(null, null, startDate, endDate, 0, 10);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("모차르트");
        assertThat(result.getContent().get(0).getContent()).isEqualTo("모차르트의 연주");
    }

    @Test
    void 기간안에_존재하는_티켓이_존재하지_않으면_빈페이지를_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "모차르트",
                "모차르트의 연주",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "뮤지컬");
        elasticsearchRepository.save(ticket);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().minusDays(3);

        Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter(null, null, startDate, endDate, 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void 조건이_모두_만족하지_않으면_빈페이지를_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "오페라의 유령",
                "오페라의 유령",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "뮤지컬");
        elasticsearchRepository.save(ticket);

        String title = "오페라의 유령";
        String category = "뮤지컬";
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now().minusDays(1);

        Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter(title, category, startDate, endDate, 0, 10);

        assertThat(result).isEmpty();
    }

    @Test
    void 추천완성_검색어를_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "박효신 콘서트",
                "박효신의 야생화",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "콘서트");
        elasticsearchRepository.save(ticket);

        TicketDocument ticket2 = new TicketDocument(
                2L,
                "이석훈 콘서트",
                "이석훈의 노래",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "콘서트");
        elasticsearchRepository.save(ticket2);

        List<SearchAutoCompleteResponse> result = ticketSearchService.findAutoCompleteSuggestionByKeyword("콘서트");

        assertThat(result).hasSize(2);
    }

    @Test
    void 추천완성_검색어가_존재하지_않으면_빈_리스트_반환한다() {
        TicketDocument ticket = new TicketDocument(
                1L,
                "아이유 콘서트",
                "아이유의 노래",
                "120분",
                LocalDateTime.now(),
                LocalDateTime.now(),
                "세종문화회관",
                "콘서트");
        elasticsearchRepository.save(ticket);

        List<SearchAutoCompleteResponse> result = ticketSearchService.findAutoCompleteSuggestionByKeyword("닭갈비");

        assertThat(result).isEmpty();
    }
}
