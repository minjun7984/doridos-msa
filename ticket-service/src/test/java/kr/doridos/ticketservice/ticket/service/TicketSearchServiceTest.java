package kr.doridos.ticketservice.ticket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import kr.doridos.ticketservice.ticket.dto.SearchAutoCompleteResponse;
import kr.doridos.ticketservice.ticket.entity.TicketDocument;
import kr.doridos.ticketservice.ticket.repository.TicketElasticsearchRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TicketSearchServiceTest {

    @Mock
    private TicketElasticsearchRepository ticketElasticsearchRepository;

    @InjectMocks
    private TicketSearchService ticketSearchService;

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @DisplayName("티켓 검색 테스트")
    @Nested
    class TicketSearch {

        @Test
        void 타이틀_검색시_해당하는_티켓이_존재하면_티켓리스트를_반환한다() {
            // given
            String keyword = "모차르트";
            List<TicketDocument> documents = List.of(
                    new TicketDocument(
                            1L,
                            "모차르트",
                            "모차르트의 연주",
                            "120분",
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            "세종문화회관",
                            "뮤지컬")
            );

            given(ticketElasticsearchRepository.findByTitle(keyword)).willReturn(documents);
            List<TicketDocument> result = ticketSearchService.searchTicketsByKeyword(keyword);

            assertThat(result).hasSize(1);
            assertThat(result).containsExactlyElementsOf(documents);
            verify(ticketElasticsearchRepository).findByTitle(keyword);
        }

        @Test
        void 타이틀_검색시_해당하는_티켓이_존재하지_않으면_빈_리스트를_반환한다() {
            String keyword = "모차르트";

            given(ticketElasticsearchRepository.findByTitle(keyword)).willReturn(List.of());
            List<TicketDocument> result = ticketSearchService.searchTicketsByKeyword(keyword);

            assertThat(result).hasSize(0);
            verify(ticketElasticsearchRepository).findByTitle(keyword);
        }
    }

    @Nested
    @DisplayName("자동완성 검색 테스트")
    class AutoCompleteSearch {

        @Test
        void 자동완성_검색시_오류가_발생하면_빈_리스트를_반환한다() throws IOException {
            String keyword = "모차";
            given(elasticsearchClient.search((SearchRequest) any(), eq(SearchAutoCompleteResponse.class)))
                    .willThrow(new IOException("Elasticsearch 검색 오류"));

            List<SearchAutoCompleteResponse> result = ticketSearchService.findAutoCompleteSuggestionByKeyword(keyword);

            assertThat(result).isEmpty();
            verify(elasticsearchClient).search((SearchRequest) any(), eq(SearchAutoCompleteResponse.class));
        }
    }

    @Nested
    @DisplayName("필터링 검색 테스트")
    class FilteredSearch {

        @Test
        void 필터링_검색시_오류가_발생하면_빈_페이지를_반환한다() throws IOException {
            // given
            String title = "모차르트";
            String category = "뮤지컬";
            LocalDateTime startDate = LocalDateTime.of(2024, 3, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2024, 3, 31, 23, 59);
            int page = 0, size = 10;

            given(elasticsearchClient.search((SearchRequest) any(), eq(TicketDocument.class)))
                    .willThrow(new IOException("Elasticsearch 검색 오류"));

            Page<TicketDocument> result = ticketSearchService.searchTicketsByFilter(title, category, startDate, endDate, page, size);

            assertThat(result.getContent()).isEmpty();
            verify(elasticsearchClient).search((SearchRequest) any(), eq(TicketDocument.class));
        }
    }
}
