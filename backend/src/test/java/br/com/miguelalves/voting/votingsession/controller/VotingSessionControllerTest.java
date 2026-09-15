package br.com.miguelalves.voting.votingsession.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import br.com.miguelalves.voting.core.exceptions.GlobalExceptionHandler;
import br.com.miguelalves.voting.core.exceptions.ProposalNotFoundException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionAlreadyExistsException;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionRequest;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;
import br.com.miguelalves.voting.votingsession.service.VotingSessionService;

@WebMvcTest(VotingSessionController.class)
@Import(GlobalExceptionHandler.class)
class VotingSessionControllerTest {

    private static final String ENDPOINT = "/api/v1/proposals/10/sessions";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VotingSessionService votingSessionService;

    @Test
    void shouldOpenVotingSession() throws Exception {
        var response = createResponse(5);

        when(votingSessionService.open(any(), any()))
                .thenReturn(response);

        mockMvc.perform(
                post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "durationInMinutes": 5
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.proposalId").value(10))
                .andExpect(jsonPath("$.startsAt")
                        .value("2026-09-15T13:00:00"))
                .andExpect(jsonPath("$.endsAt")
                        .value("2026-09-15T13:05:00"));
    }

    @Test
    void shouldReturnNotFoundWhenProposalDoesNotExist() throws Exception {
        when(votingSessionService.open(any(), any()))
                .thenThrow(new ProposalNotFoundException(10L));

        mockMvc.perform(
                post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "durationInMinutes": 5
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message")
                        .value("Proposal with ID 10 was not found"))
                .andExpect(jsonPath("$.path").value(ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturnConflictWhenVotingSessionAlreadyExists() throws Exception {
        when(votingSessionService.open(any(), any()))
                .thenThrow(new VotingSessionAlreadyExistsException(10L));

        mockMvc.perform(
                post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "durationInMinutes": 5
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message")
                        .value("A voting session already exists for proposal 10"))
                .andExpect(jsonPath("$.path").value(ENDPOINT))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldOpenVotingSessionWithNullDuration() throws Exception {
        var response = createResponse(1);

        when(votingSessionService.open(any(), any()))
                .thenReturn(response);

        mockMvc.perform(
                post(ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());

        verify(votingSessionService).open(
                10L,
                new OpenVotingSessionRequest(null));
    }

    private VotingSessionResponse createResponse(long durationInMinutes) {
        var startsAt = LocalDateTime.of(2026, 9, 15, 13, 0);
        return new VotingSessionResponse(
                1L,
                10L,
                startsAt,
                startsAt.plusMinutes(durationInMinutes));
    }
}
