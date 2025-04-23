package kr.doridos.ticketservice.ticket.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.doridos.ticketservice.ticket.dto.TicketCreateRequest;
import kr.doridos.ticketservice.ticket.dto.TicketUpdateRequest;
import kr.doridos.ticketservice.ticket.fixture.TicketFixture;
import kr.doridos.ticketservice.ticket.fixture.TokenFixture;
import kr.doridos.ticketservice.ticket.repository.TicketRepository;

import kr.doridos.ticketservice.util.IntegrationTestSupport;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TicketManagerControllerTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TicketRepository ticketRepository;

    String userToken;
    String ticketManagerToken;
    String secondTicketManagerToken;

    @BeforeEach
    void setUp() {
        ticketManagerToken = TokenFixture.티켓매니저_토큰_생성();
        secondTicketManagerToken = TokenFixture.티켓매니저2_토큰_생성();
        userToken = TokenFixture.일반유저_토큰_생성();
        ticketRepository.save(TicketFixture.티켓_생성());

    }

    @Test
    void 티켓생성에_성공한다201() throws Exception {
        TicketCreateRequest ticketCreateRequest = TicketFixture.티켓_생성_요청();

        mockMvc.perform(post("/api/v1/manager/tickets")
                        .header("Authorization", "Bearer " + ticketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isCreated())
                .andDo(document("ticketCreate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    @DisplayName("티켓 생성시 일반유저라면 예외가 발생한다. - 401")
    void 티켓_생성시_일반유저라면_예외가_발생한다401() throws Exception {
        TicketCreateRequest ticketCreateRequest = TicketFixture.티켓_생성_요청();

        mockMvc.perform(post("/api/v1/manager/tickets")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("권한이 없는 사용자입니다."))
                .andDo(document("ticketCreateFail",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 티켓_생성시_카테고리가_존재하지_않으면_예외가_발생한다401() throws Exception {
        TicketCreateRequest ticketCreateRequest = TicketCreateRequest.builder()
                .title("모차르트")
                .content("모차르트 최고의 연주")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2023, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .startDate(LocalDateTime.of(2023, 7, 23, 13, 0))
                .placeId(1L)
                .categoryId(100L).build();

        mockMvc.perform(post("/api/v1/manager/tickets")
                        .header("Authorization", "Bearer " + ticketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("카테고리가 존재하지 않습니다."));
    }

    @Test
    void 티켓_생성시_장소가_존재하지_않으면_예외가_발생한다401() throws Exception {
        TicketCreateRequest ticketCreateRequest = TicketCreateRequest.builder()
                .title("모차르트")
                .content("모차르트 최고의 연주")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2023, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2023, 7, 23, 12, 0))
                .startDate(LocalDateTime.of(2023, 7, 23, 13, 0))
                .placeId(100L)
                .categoryId(1L).build();

        mockMvc.perform(post("/api/v1/manager/tickets")
                        .header("Authorization", "Bearer " + ticketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketCreateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("장소가 존재하지 않습니다."));
    }

    @Test
    void 티켓수정에_성공한다() throws Exception {
        TicketUpdateRequest ticketUpdateRequest = TicketFixture.티켓_수정_요청();

        mockMvc.perform(patch("/api/v1/manager/tickets/" + 1)
                        .header("Authorization", "Bearer " + ticketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketUpdateRequest)))
                .andExpect(status().isNoContent())
                .andDo(document("ticketUpdate",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())
                ));
    }

    @Test
    void 티켓수정시_예미시작시간과_마감시간이_유효하지_않으면_예외가_발생한다400() throws Exception {
        TicketUpdateRequest ticketUpdateRequest = TicketUpdateRequest.builder()
                .title("호차르트")
                .content("호호차차르트")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2024, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2024, 7, 21, 12, 0))
                .startDate(LocalDateTime.of(2024, 7, 23, 13, 0))
                .build();

        mockMvc.perform(patch("/api/v1/manager/tickets/" + 1)
                        .header("Authorization", "Bearer " + ticketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketUpdateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("시작일은 종료일 이후가 될 수 없습니다."));
    }

    @Test
    void 티켓수정시_작성자가_아니라면_예외가_발생한다400() throws Exception {
        TicketUpdateRequest ticketUpdateRequest = TicketUpdateRequest.builder()
                .title("호차르트")
                .content("호호차차르트")
                .runningTime("120분")
                .openDate(LocalDateTime.of(2024, 7, 22, 12, 0))
                .endDate(LocalDateTime.of(2024, 7, 21, 12, 0))
                .startDate(LocalDateTime.of(2024, 7, 23, 13, 0))
                .build();

        mockMvc.perform(patch("/api/v1/manager/tickets/" + 1)
                        .header("Authorization", "Bearer " + secondTicketManagerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ticketUpdateRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("권한이 없는 유저입니다."));
    }
}
