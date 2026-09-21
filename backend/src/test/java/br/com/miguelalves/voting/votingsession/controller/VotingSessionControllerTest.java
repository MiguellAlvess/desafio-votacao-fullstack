package br.com.miguelalves.voting.votingsession.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.miguelalves.voting.core.exceptions.GlobalExceptionHandler;
import br.com.miguelalves.voting.core.exceptions.ProposalNotFoundException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionAlreadyExistsException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionNotFoundException;
import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionDetailsResponse;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionResponse;
import br.com.miguelalves.voting.votingsession.service.VotingSessionService;

@WebMvcTest(VotingSessionController.class)
@Import(GlobalExceptionHandler.class)
class VotingSessionControllerTest {

        private static final String ENDPOINT = "/api/v1/proposals/10/sessions";
        private static final String OPEN_ENDPOINT = "/api/v1/voting-sessions/open";
        private static final String DETAILS_ENDPOINT = "/api/v1/voting-sessions/1";

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
        }

        @Test
        void shouldReturnOpenVotingSessions() throws Exception {
                var response = new OpenVotingSessionResponse(
                                1L,
                                10L,
                                "Aquisição de novos equipamentos",
                                LocalDateTime.of(2026, 9, 16, 10, 0),
                                LocalDateTime.of(2026, 9, 16, 10, 10));
                when(votingSessionService.findOpenSessions())
                                .thenReturn(List.of(response));

                mockMvc.perform(get(OPEN_ENDPOINT))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].id").value(1))
                                .andExpect(jsonPath("$[0].proposalId").value(10))
                                .andExpect(jsonPath("$[0].proposalTitle")
                                                .value("Aquisição de novos equipamentos"))
                                .andExpect(jsonPath("$[0].startsAt")
                                                .value("2026-09-16T10:00:00"))
                                .andExpect(jsonPath("$[0].endsAt")
                                                .value("2026-09-16T10:10:00"));
        }

        @Test
        void shouldReturnEmptyListWhenThereAreNoOpenSessions() throws Exception {
                when(votingSessionService.findOpenSessions())
                                .thenReturn(List.of());

                mockMvc.perform(get(OPEN_ENDPOINT))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        void shouldReturnVotingSessionById() throws Exception {
                var response = new VotingSessionDetailsResponse(
                                1L,
                                10L,
                                "Annual budget approval",
                                "Voting for approval of the annual budget",
                                LocalDateTime.of(2026, 9, 21, 13, 0),
                                LocalDateTime.of(2026, 9, 21, 13, 5),
                                VotingSessionStatus.OPEN);
                when(votingSessionService.findById(1L)).thenReturn(response);

                mockMvc.perform(get(DETAILS_ENDPOINT))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.proposalId").value(10))
                                .andExpect(jsonPath("$.proposalTitle")
                                                .value("Annual budget approval"))
                                .andExpect(jsonPath("$.proposalDescription")
                                                .value("Voting for approval of the annual budget"))
                                .andExpect(jsonPath("$.startsAt")
                                                .value("2026-09-21T13:00:00"))
                                .andExpect(jsonPath("$.endsAt")
                                                .value("2026-09-21T13:05:00"))
                                .andExpect(jsonPath("$.status").value("OPEN"));
        }

        @Test
        void shouldReturnNotFoundWhenVotingSessionDoesNotExist() throws Exception {
                when(votingSessionService.findById(1L))
                                .thenThrow(new VotingSessionNotFoundException(1L));

                mockMvc.perform(get(DETAILS_ENDPOINT))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message")
                                                .value("Voting session with ID 1 was not found"))
                                .andExpect(jsonPath("$.path").value(DETAILS_ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
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
