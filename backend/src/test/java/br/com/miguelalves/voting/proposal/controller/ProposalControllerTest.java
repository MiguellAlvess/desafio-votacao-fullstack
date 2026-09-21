package br.com.miguelalves.voting.proposal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.miguelalves.voting.core.exceptions.GlobalExceptionHandler;
import br.com.miguelalves.voting.proposal.dto.ProposalManagementResponse;
import br.com.miguelalves.voting.proposal.dto.ProposalResponse;
import br.com.miguelalves.voting.proposal.service.ProposalService;
import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionSummaryResponse;

@WebMvcTest(ProposalController.class)
@Import(GlobalExceptionHandler.class)
class ProposalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProposalService proposalService;

    @Test
    void shouldCreateProposal() throws Exception {
        var createdAt = LocalDateTime.of(2026, 9, 15, 10, 30);
        var response = new ProposalResponse(
                1L,
                "Annual budget approval",
                "Voting for approval of the annual budget",
                createdAt);

        when(proposalService.create(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/api/v1/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Annual budget approval",
                                  "description": "Voting for approval of the annual budget"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Annual budget approval"))
                .andExpect(jsonPath("$.description")
                        .value("Voting for approval of the annual budget"))
                .andExpect(jsonPath("$.createdAt")
                        .value("2026-09-15T10:30:00"));
    }

    @Test
    void shouldListProposals() throws Exception {
        var firstResponse = new ProposalResponse(
                1L,
                "Annual budget approval",
                "Voting for approval of the annual budget",
                LocalDateTime.of(2026, 9, 15, 10, 30));
        var secondResponse = new ProposalResponse(
                2L,
                "Board election",
                null,
                LocalDateTime.of(2026, 9, 15, 11, 0));

        when(proposalService.findAll())
                .thenReturn(List.of(firstResponse, secondResponse));

        mockMvc.perform(get("/api/v1/proposals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title")
                        .value("Annual budget approval"))
                .andExpect(jsonPath("$[0].description")
                        .value("Voting for approval of the annual budget"))
                .andExpect(jsonPath("$[0].createdAt")
                        .value("2026-09-15T10:30:00"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title")
                        .value("Board election"))
                .andExpect(jsonPath("$[1].description").isEmpty())
                .andExpect(jsonPath("$[1].createdAt")
                        .value("2026-09-15T11:00:00"));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        mockMvc.perform(
                post("/api/v1/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "description": "Description"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("title: Proposal title cannot be blank"))
                .andExpect(jsonPath("$.path")
                        .value("/api/v1/proposals"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnProposalsForManagement() throws Exception {
        var withoutSession = new ProposalManagementResponse(
                1L,
                "Proposal without session",
                null,
                LocalDateTime.of(2026, 9, 21, 9, 0),
                null);
        var openSession = new ProposalManagementResponse(
                2L,
                "Proposal with open session",
                "Description",
                LocalDateTime.of(2026, 9, 21, 9, 30),
                new VotingSessionSummaryResponse(
                        10L,
                        VotingSessionStatus.OPEN,
                        LocalDateTime.of(2026, 9, 21, 10, 0),
                        LocalDateTime.of(2026, 9, 21, 10, 5)));
        var closedSession = new ProposalManagementResponse(
                3L,
                "Proposal with closed session",
                null,
                LocalDateTime.of(2026, 9, 20, 9, 0),
                new VotingSessionSummaryResponse(
                        11L,
                        VotingSessionStatus.CLOSED,
                        LocalDateTime.of(2026, 9, 20, 10, 0),
                        LocalDateTime.of(2026, 9, 20, 10, 5)));
        when(proposalService.findAllForManagement())
                .thenReturn(List.of(withoutSession, openSession, closedSession));

        mockMvc.perform(get("/api/v1/proposals/management"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].session").doesNotExist())
                .andExpect(jsonPath("$[1].session.id").value(10))
                .andExpect(jsonPath("$[1].session.status").value("OPEN"))
                .andExpect(jsonPath("$[1].session.startsAt").value("2026-09-21T10:00:00"))
                .andExpect(jsonPath("$[1].session.endsAt").value("2026-09-21T10:05:00"))
                .andExpect(jsonPath("$[2].session.id").value(11))
                .andExpect(jsonPath("$[2].session.status").value("CLOSED"));
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoProposals() throws Exception {
        when(proposalService.findAllForManagement()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/proposals/management"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
