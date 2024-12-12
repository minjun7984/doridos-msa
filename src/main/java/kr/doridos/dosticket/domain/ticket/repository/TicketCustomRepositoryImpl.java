package kr.doridos.dosticket.domain.ticket.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.doridos.dosticket.domain.category.entity.QCategory;
import kr.doridos.dosticket.domain.ticket.dto.QTicketPageResponse;
import kr.doridos.dosticket.domain.ticket.dto.TicketPageResponse;
import kr.doridos.dosticket.domain.ticket.entity.QTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

public class TicketCustomRepositoryImpl implements TicketCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public TicketCustomRepositoryImpl(final JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<TicketPageResponse> findFilteredTickets(LocalDate startDate, LocalDate endDate, Long categoryId, Pageable pageable) {
        QTicket qTicket = QTicket.ticket;

        Predicate condition = createCondition(qTicket, startDate, endDate, categoryId);
        List<Long> ids = fetchTicketIds(qTicket, condition, pageable);

        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        List<TicketPageResponse> ticketPageResponse = fetchTicketsByIds(qTicket, ids);
        JPAQuery<Long> countQuery = createCountQuery(qTicket, condition);

        return PageableExecutionUtils.getPage(ticketPageResponse, pageable, countQuery::fetchOne);
    }

    private Predicate createCondition(QTicket qTicket, LocalDate startDate, LocalDate endDate, Long categoryId) {
        BooleanExpression condition = qTicket.isNotNull(); // 초기 조건 설정
        QCategory qCategory = QCategory.category;

        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
            condition = condition.and(qTicket.startDate.between(startDateTime, endDateTime));
        }

        if (categoryId != null) {
            List<Long> categoryIds = fetchCategoryIdsByParentId(categoryId, qCategory);
            condition = condition.and(qTicket.category.id.in(categoryIds));
        }

        return condition;
    }

    private List<Long> fetchTicketIds(QTicket qTicket, Predicate condition, Pageable pageable) {
        return jpaQueryFactory
                .select(qTicket.id)
                .from(qTicket)
                .where(condition)
                .orderBy(qTicket.startDate.desc()) // 페이징을 위한 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private List<TicketPageResponse> fetchTicketsByIds(QTicket qTicket, List<Long> ids) {
        return jpaQueryFactory
                .select(new QTicketPageResponse(
                        qTicket.id,
                        qTicket.title,
                        qTicket.content,
                        qTicket.runningTime,
                        qTicket.openDate,
                        qTicket.endDate,
                        qTicket.startDate
                ))
                .from(qTicket)
                .where(qTicket.id.in(ids))
                .orderBy(qTicket.startDate.desc())
                .fetch();
    }

    private JPAQuery<Long> createCountQuery(QTicket qTicket, Predicate condition) {
        return jpaQueryFactory
                .select(qTicket.count())
                .from(qTicket)
                .where(condition);
    }

    private List<Long> fetchCategoryIdsByParentId(Long categoryId, QCategory category) {
        return jpaQueryFactory
                .select(category.id)
                .from(category)
                .where(category.parent.id.eq(categoryId).or(category.id.eq(categoryId)))
                .fetch();
    }
}
