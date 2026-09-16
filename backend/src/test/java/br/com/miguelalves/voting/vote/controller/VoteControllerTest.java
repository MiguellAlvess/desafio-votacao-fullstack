package br.com.miguelalves.voting.vote.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.miguelalves.voting.core.exceptions.AssociateAlreadyVotedException;
import br.com.miguelalves.voting.core.exceptions.AssociateNotFoundException;
import br.com.miguelalves.voting.core.exceptions.AssociateUnableToVoteException;
import br.com.miguelalves.voting.core.exceptions.GlobalExceptionHandler;
import br.com.miguelalves.voting.core.exceptions.InvalidCpfException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionClosedException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionNotFoundException;
import br.com.miguelalves.voting.vote.domain.VoteChoice;
import br.com.miguelalves.voting.vote.dto.CreateVoteRequest;
import br.com.miguelalves.voting.vote.dto.VoteResponse;
import br.com.miguelalves.voting.vote.dto.VotingResultResponse;
import br.com.miguelalves.voting.vote.service.VoteService;

@WebMvcTest(VoteController.class)
@Import(GlobalExceptionHandler.class)
class VoteControllerTest {

        private static final String ENDPOINT = "/api/v1/voting-sessions/2/votes";
        private static final String RESULT_ENDPOINT = "/api/v1/voting-sessions/2/result";
        private static final String VALID_REQUEST = """
                        {
                          "associateId": 1,
                          "choice": "YES"
                        }
                        """;

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private VoteService voteService;

        @Test
        void shouldRegisterVote() throws Exception {
                var response = new VoteResponse(
                                10L,
                                2L,
                                1L,
                                VoteChoice.YES,
                                LocalDateTime.of(2026, 9, 16, 10, 0));
                when(voteService.vote(2L, new CreateVoteRequest(1L, VoteChoice.YES)))
                                .thenReturn(response);

                postVote(VALID_REQUEST)
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(10))
                                .andExpect(jsonPath("$.votingSessionId").value(2))
                                .andExpect(jsonPath("$.associateId").value(1))
                                .andExpect(jsonPath("$.choice").value("YES"))
                                .andExpect(jsonPath("$.createdAt")
                                                .value("2026-09-16T10:00:00"));

        }

        @Test
        void shouldReturnNotFoundWhenVotingSessionDoesNotExist() throws Exception {
                when(voteService.vote(2L, new CreateVoteRequest(1L, VoteChoice.YES)))
                                .thenThrow(new VotingSessionNotFoundException(2L));

                postVote(VALID_REQUEST)
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message")
                                                .value("Voting session with ID 2 was not found"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnNotFoundWhenAssociateDoesNotExist() throws Exception {
                when(voteService.vote(2L, new CreateVoteRequest(1L, VoteChoice.YES)))
                                .thenThrow(new AssociateNotFoundException(1L));

                postVote(VALID_REQUEST)
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message")
                                                .value("Associate with ID 1 was not found"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnConflictWhenVotingSessionIsClosed() throws Exception {
                when(voteService.vote(2L, new CreateVoteRequest(1L, VoteChoice.YES)))
                                .thenThrow(new VotingSessionClosedException(2L));

                postVote(VALID_REQUEST)
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409))
                                .andExpect(jsonPath("$.error").value("Conflict"))
                                .andExpect(jsonPath("$.message")
                                                .value("Voting session 2 is closed"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnConflictWhenAssociateAlreadyVoted() throws Exception {
                when(voteService.vote(2L, new CreateVoteRequest(1L, VoteChoice.YES)))
                                .thenThrow(new AssociateAlreadyVotedException(1L, 2L));

                postVote(VALID_REQUEST)
                                .andExpect(status().isConflict())
                                .andExpect(jsonPath("$.status").value(409))
                                .andExpect(jsonPath("$.error").value("Conflict"))
                                .andExpect(jsonPath("$.message")
                                                .value("Associate 1 has already voted in voting session 2"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnNotFoundWhenCpfIsInvalid() throws Exception {
                when(voteService.vote(2L, new CreateVoteRequest(1L, VoteChoice.YES)))
                                .thenThrow(new InvalidCpfException("12345678901"));

                postVote(VALID_REQUEST)
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message")
                                                .value("CPF 12345678901 is invalid"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnNotFoundWhenAssociateIsUnableToVote() throws Exception {
                when(voteService.vote(2L, new CreateVoteRequest(1L, VoteChoice.YES)))
                                .thenThrow(new AssociateUnableToVoteException("52998224725"));

                postVote(VALID_REQUEST)
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message")
                                                .value("Associate with CPF 52998224725 is unable to vote"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnBadRequestWhenAssociateIdIsMissing() throws Exception {
                postVote("""
                                {"choice": "YES"}
                                """)
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message")
                                                .value("associateId: Associate ID cannot be null"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnBadRequestWhenChoiceIsMissing() throws Exception {
                postVote("""
                                {"associateId": 1}
                                """)
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value(400))
                                .andExpect(jsonPath("$.error").value("Bad Request"))
                                .andExpect(jsonPath("$.message")
                                                .value("choice: Vote choice cannot be null"))
                                .andExpect(jsonPath("$.path").value(ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        @Test
        void shouldReturnVotingResult() throws Exception {
                var response = new VotingResultResponse(
                                2L,
                                3,
                                2,
                                5);
                when(voteService.getResult(2L)).thenReturn(response);

                mockMvc.perform(get(RESULT_ENDPOINT))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.votingSessionId").value(2))
                                .andExpect(jsonPath("$.yesVotes").value(3))
                                .andExpect(jsonPath("$.noVotes").value(2))
                                .andExpect(jsonPath("$.totalVotes").value(5))
                                .andExpect(jsonPath("$.result").doesNotExist());
        }

        @Test
        void shouldReturnNotFoundWhenVotingSessionDoesNotExistForResult() throws Exception {
                when(voteService.getResult(2L))
                                .thenThrow(new VotingSessionNotFoundException(2L));

                mockMvc.perform(get(RESULT_ENDPOINT))
                                .andExpect(status().isNotFound())
                                .andExpect(jsonPath("$.status").value(404))
                                .andExpect(jsonPath("$.error").value("Not Found"))
                                .andExpect(jsonPath("$.message")
                                                .value("Voting session with ID 2 was not found"))
                                .andExpect(jsonPath("$.path").value(RESULT_ENDPOINT))
                                .andExpect(jsonPath("$.timestamp").exists());
        }

        private ResultActions postVote(String request) throws Exception {
                return mockMvc.perform(
                                post(ENDPOINT)
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(request));
        }
}
