package kr.doridos.ticketservice.ticket.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import kr.doridos.ticketservice.ticket.dto.SearchAutoCompleteResponse;
import kr.doridos.ticketservice.ticket.entity.TicketDocument;
import kr.doridos.ticketservice.ticket.repository.TicketElasticsearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
public class TicketSearchService {
    private static final DateTimeFormatter ELASTICSEARCH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final TicketElasticsearchRepository ticketElasticsearchRepository;
    private final ElasticsearchClient elasticsearchClient;

    public TicketSearchService(final TicketElasticsearchRepository ticketElasticsearchRepository, final ElasticsearchClient elasticsearchClient) {
        this.ticketElasticsearchRepository = ticketElasticsearchRepository;
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<TicketDocument> searchTicketsByKeyword(String keyword) {
        return ticketElasticsearchRepository.findByTitle(keyword);
    }

    public List<SearchAutoCompleteResponse> findAutoCompleteSuggestionByKeyword(String keyword) {
        try {
            SearchRequest searchRequest = new SearchRequest.Builder()
                    .index("tickets")
                    .query(q -> q
                            .matchPhrase(m -> m
                                    .field("title.ngram")
                                    .query(keyword)
                            )
                    )
                    .size(5)
                    .build();

            SearchResponse<SearchAutoCompleteResponse> searchResponse = elasticsearchClient.search(searchRequest, SearchAutoCompleteResponse.class);

            return searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("Elasticsearch 검색 중 오류 발생: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Page<TicketDocument> searchTicketsByFilter(String title, String category, LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        try {
            Query query = buildSearchQuery(title, category, startDate, endDate);
            SearchRequest searchRequest = SearchRequest.of(s -> s
                    .index("tickets")
                    .query(query)
                    .from(page * size)
                    .size(size)
            );

            SearchResponse<TicketDocument> searchResponse = elasticsearchClient.search(searchRequest, TicketDocument.class);

            List<TicketDocument> tickets = searchResponse.hits().hits().stream()
                    .map(Hit::source)
                    .toList();

            return new PageImpl<>(tickets, pageable, searchResponse.hits().total().value());

        } catch (IOException e) {
            log.error("Elasticsearch 검색 중 오류 발생: {}", e.getMessage(), e);
            return Page.empty(pageable);
        }
    }

    private Query buildSearchQuery(String title, String category, LocalDateTime startDate, LocalDateTime endDate) {
        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        if (title != null) boolQuery.must(MatchQuery.of(m -> m.field("title.ngram").query(title))._toQuery());
        if (category != null) boolQuery.must(TermQuery.of(t -> t.field("category").value(category))._toQuery());
        if (startDate != null && endDate != null) {
            boolQuery.must(QueryBuilders.range().field("startDate")
                    .gte(JsonData.of(ELASTICSEARCH_DATE_FORMATTER.format(startDate)))
                    .lte(JsonData.of(ELASTICSEARCH_DATE_FORMATTER.format(endDate)))
                    .build()._toQuery());
        }
        return boolQuery.build()._toQuery();
    }
}
