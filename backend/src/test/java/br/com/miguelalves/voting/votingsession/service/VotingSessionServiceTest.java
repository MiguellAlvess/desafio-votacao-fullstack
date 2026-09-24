package br.com.miguelalves.voting.votingsession.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.miguelalves.voting.core.exceptions.ProposalNotFoundException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionAlreadyExistsException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionNotFoundException;
import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionRequest;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionResponse;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionDetailsResponse;
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
        }

        @Test
        void shouldReturnOpenVotingSessions() {
                var first = new VotingSession(
                                new Proposal("First proposal", null),
                                Duration.ofMinutes(5),
                                LocalDateTime.now().minusMinutes(1));
                var second = new VotingSession(
                                new Proposal("Second proposal", null),
                                Duration.ofMinutes(10),
                                LocalDateTime.now().minusMinutes(1));
                var firstResponse = new OpenVotingSessionResponse(
                                1L, 10L, "First proposal", first.startsAt(), first.endsAt());
                var secondResponse = new OpenVotingSessionResponse(
                                2L, 20L, "Second proposal", second.startsAt(), second.endsAt());
                when(votingSessionRepository.findAllByStartsAtLessThanEqualAndEndsAtAfter(
                                any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(List.of(first, second));
                when(votingSessionMapper.toOpenResponse(first)).thenReturn(firstResponse);
                when(votingSessionMapper.toOpenResponse(second)).thenReturn(secondResponse);

                var responses = votingSessionService.findOpenSessions();

                assertThat(responses).containsExactly(firstResponse, secondResponse);
                var time = ArgumentCaptor.forClass(LocalDateTime.class);
                verify(votingSessionRepository)
                                .findAllByStartsAtLessThanEqualAndEndsAtAfter(
                                                time.capture(), time.capture());
                assertThat(time.getAllValues().get(0))
                                .isEqualTo(time.getAllValues().get(1));
        }

        @Test
        void shouldReturnEmptyListWhenThereAreNoOpenSessions() {
                when(votingSessionRepository.findAllByStartsAtLessThanEqualAndEndsAtAfter(
                                any(LocalDateTime.class), any(LocalDateTime.class)))
                                .thenReturn(List.of());

                assertThat(votingSessionService.findOpenSessions()).isEmpty();
        }

        @Test
        void shouldReturnVotingSessionById() {
                var sessionId = 1L;
                var session = new VotingSession(
                                createProposal(),
                                Duration.ofMinutes(5),
                                LocalDateTime.now());
                var expectedResponse = new VotingSessionDetailsResponse(
                                sessionId,
                                10L,
                                "Annual budget approval",
                                "Voting for approval of the annual budget",
                                session.startsAt(),
                                session.endsAt(),
                                VotingSessionStatus.OPEN);
                when(votingSessionRepository.findDetailsById(sessionId))
                                .thenReturn(Optional.of(session));
                when(votingSessionMapper.toDetailsResponse(
                                any(VotingSession.class),
                                any(LocalDateTime.class)))
                                .thenReturn(expectedResponse);

                var result = votingSessionService.findById(sessionId);

                assertThat(result).isEqualTo(expectedResponse);
        }

        @Test
        void shouldReturnOpenStatusWhenSessionIsOpen() {
                var session = new VotingSession(
                                createProposal(),
                                Duration.ofMinutes(5),
                                LocalDateTime.now());
                when(votingSessionRepository.findDetailsById(1L))
                                .thenReturn(Optional.of(session));
                var service = new VotingSessionService(
                                votingSessionRepository,
                                proposalRepository,
                                new VotingSessionMapper());

                var result = service.findById(1L);

                assertThat(result.status()).isEqualTo(VotingSessionStatus.OPEN);
        }

        @Test
        void shouldReturnClosedStatusWhenSessionIsClosed() {
                var session = new VotingSession(
                                createProposal(),
                                Duration.ofMinutes(1),
                                LocalDateTime.now().minusMinutes(2));
                when(votingSessionRepository.findDetailsById(1L))
                                .thenReturn(Optional.of(session));
                var service = new VotingSessionService(
                                votingSessionRepository,
                                proposalRepository,
                                new VotingSessionMapper());

                var result = service.findById(1L);

                assertThat(result.status()).isEqualTo(VotingSessionStatus.CLOSED);
        }

        @Test
        void shouldThrowNotFoundWhenVotingSessionDoesNotExist() {
                when(votingSessionRepository.findDetailsById(1L))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> votingSessionService.findById(1L))
                                .isInstanceOf(VotingSessionNotFoundException.class)
                                .hasMessage("Voting session with ID 1 was not found");
        }

        private Proposal createProposal() {
                return new Proposal(
                                "Annual budget approval",
                                "Voting for approval of the annual budget");
        }
}
