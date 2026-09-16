package br.com.miguelalves.voting.e2e;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.jayway.jsonpath.JsonPath;

import br.com.miguelalves.voting.associate.repository.AssociateRepository;
import br.com.miguelalves.voting.external.eligibility.AssociateEligibilityClient;
import br.com.miguelalves.voting.external.eligibility.VotingEligibility;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;
import br.com.miguelalves.voting.vote.repository.VoteRepository;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class VotingFlowE2ETest {

    private static final String CPF = "52998224725";

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private AssociateRepository associateRepository;

    @MockitoBean
    private AssociateEligibilityClient associateEligibilityClient;

    @BeforeEach
    void cleanDatabase() {
        voteRepository.deleteAllInBatch();
        votingSessionRepository.deleteAllInBatch();
        proposalRepository.deleteAllInBatch();
        associateRepository.deleteAllInBatch();
    }

    @Test
    void shouldCompleteVotingFlow() throws Exception {
        when(associateEligibilityClient.check(CPF)).thenReturn(VotingEligibility.ABLE_TO_VOTE);
        var associateId = identifyAssociate();
        var proposalId = createProposal();
        var sessionId = openVotingSession(proposalId);

        registerVote(sessionId, associateId)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.votingSessionId").value(sessionId.intValue()))
                .andExpect(jsonPath("$.associateId").value(associateId.intValue()))
                .andExpect(jsonPath("$.choice").value("YES"));

        mockMvc.perform(get("/api/v1/voting-sessions/{id}/result", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.votingSessionId").value(sessionId.intValue()))
                .andExpect(jsonPath("$.yesVotes").value(1))
                .andExpect(jsonPath("$.noVotes").value(0))
                .andExpect(jsonPath("$.totalVotes").value(1));
    }

    @Test
    void shouldRejectDuplicateVote() throws Exception {
        when(associateEligibilityClient.check(CPF)).thenReturn(VotingEligibility.ABLE_TO_VOTE);
        var associateId = identifyAssociate();
        var sessionId = openVotingSession(createProposal());

        registerVote(sessionId, associateId).andExpect(status().isCreated());
        registerVote(sessionId, associateId)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value(
                        "Associate " + associateId + " has already voted in voting session " + sessionId))
                .andExpect(jsonPath("$.path").value("/api/v1/voting-sessions/" + sessionId + "/votes"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldRejectVoteWhenAssociateIsUnableToVote() throws Exception {
        when(associateEligibilityClient.check(CPF)).thenReturn(VotingEligibility.UNABLE_TO_VOTE);
        var associateId = identifyAssociate();
        var sessionId = openVotingSession(createProposal());

        registerVote(sessionId, associateId)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/v1/voting-sessions/" + sessionId + "/votes"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private Long identifyAssociate() throws Exception {
        var result = mockMvc.perform(post("/api/v1/associates/identify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"cpf\":\"" + CPF + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(CPF))
                .andReturn();
        return responseId(result);
    }

    private Long createProposal() throws Exception {
        var result = mockMvc.perform(post("/api/v1/proposals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Annual budget approval\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Annual budget approval"))
                .andReturn();
        return responseId(result);
    }

    private Long openVotingSession(Long proposalId) throws Exception {
        var result = mockMvc.perform(post("/api/v1/proposals/{id}/sessions", proposalId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"durationInMinutes\":5}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.proposalId").value(proposalId.intValue()))
                .andReturn();
        return responseId(result);
    }

    private ResultActions registerVote(
            Long sessionId, Long associateId) throws Exception {
        return mockMvc.perform(post("/api/v1/voting-sessions/{id}/votes", sessionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"associateId\":" + associateId + ",\"choice\":\"YES\"}"));
    }

    private Long responseId(MvcResult result) throws Exception {
        Number id = JsonPath.read(result.getResponse().getContentAsString(), "$.id");
        return id.longValue();
    }
}
