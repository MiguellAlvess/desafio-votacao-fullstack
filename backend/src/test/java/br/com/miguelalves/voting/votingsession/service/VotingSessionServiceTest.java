package br.com.miguelalves.voting.votingsession.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.miguelalves.voting.core.exceptions.ProposalNotFoundException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionAlreadyExistsException;
import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionRequest;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;
import br.com.miguelalves.voting.votingsession.mapper.VotingSessionMapper;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@ExtendWith(MockitoExtension.class)
class VotingSessionServiceTest {

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private ProposalRepository proposalRepository;

    @Mock
    private VotingSessionMapper votingSessionMapper;

    private VotingSessionService votingSessionService;

    @BeforeEach
    void setUp() {
        votingSessionService = new VotingSessionService(
                votingSessionRepository,
                proposalRepository,
                votingSessionMapper);
    }

    @Test
    void shouldOpenVotingSession() {
        var proposalId = 1L;
        var request = new OpenVotingSessionRequest(5L);
        var proposal = createProposal();
        var savedVotingSession = new VotingSession(
                proposal,
                java.time.Duration.ofMinutes(5),
                LocalDateTime.now());
        var expectedResponse = new VotingSessionResponse(
                1L,
                proposalId,
                savedVotingSession.startsAt(),
                savedVotingSession.endsAt());

        when(proposalRepository.findById(proposalId))
                .thenReturn(Optional.of(proposal));
        when(votingSessionRepository.existsByProposalId(proposalId))
                .thenReturn(false);
        when(votingSessionRepository.save(any(VotingSession.class)))
                .thenReturn(savedVotingSession);
        when(votingSessionMapper.toResponse(savedVotingSession))
                .thenReturn(expectedResponse);

        var result = votingSessionService.open(proposalId, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(votingSessionRepository).save(any(VotingSession.class));
        verify(votingSessionMapper).toResponse(savedVotingSession);
    }

    @Test
    void shouldThrowExceptionWhenProposalDoesNotExist() {
        var proposalId = 1L;
        var request = new OpenVotingSessionRequest(5L);

        when(proposalRepository.findById(proposalId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> votingSessionService.open(proposalId, request))
                .isInstanceOf(ProposalNotFoundException.class)
                .hasMessage("Proposal with ID 1 was not found");
        verify(votingSessionRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenVotingSessionAlreadyExists() {
        var proposalId = 1L;
        var request = new OpenVotingSessionRequest(5L);

        when(proposalRepository.findById(proposalId))
                .thenReturn(Optional.of(createProposal()));
        when(votingSessionRepository.existsByProposalId(proposalId))
                .thenReturn(true);

        assertThatThrownBy(() -> votingSessionService.open(proposalId, request))
                .isInstanceOf(VotingSessionAlreadyExistsException.class)
                .hasMessage("A voting session already exists for proposal 1");
        verify(votingSessionRepository, never()).save(any());
    }

    @Test
    void shouldOpenVotingSessionWhenDurationIsNull() {
        var proposalId = 1L;
        var request = new OpenVotingSessionRequest(null);
        var proposal = createProposal();
        var savedVotingSession = new VotingSession(
                proposal,
                null,
                LocalDateTime.now());
        var expectedResponse = new VotingSessionResponse(
                1L,
                proposalId,
                savedVotingSession.startsAt(),
                savedVotingSession.endsAt());

        when(proposalRepository.findById(proposalId))
                .thenReturn(Optional.of(proposal));
        when(votingSessionRepository.save(any(VotingSession.class)))
                .thenReturn(savedVotingSession);
        when(votingSessionMapper.toResponse(savedVotingSession))
                .thenReturn(expectedResponse);

        var result = votingSessionService.open(proposalId, request);

        assertThat(result).isEqualTo(expectedResponse);
        verify(votingSessionRepository).save(any(VotingSession.class));
    }

    private Proposal createProposal() {
        return new Proposal(
                "Annual budget approval",
                "Voting for approval of the annual budget");
    }
}
