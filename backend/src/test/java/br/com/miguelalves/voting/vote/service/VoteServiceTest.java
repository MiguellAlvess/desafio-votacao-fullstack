package br.com.miguelalves.voting.vote.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.miguelalves.voting.associate.domain.Associate;
import br.com.miguelalves.voting.associate.repository.AssociateRepository;
import br.com.miguelalves.voting.core.exceptions.AssociateAlreadyVotedException;
import br.com.miguelalves.voting.core.exceptions.AssociateNotFoundException;
import br.com.miguelalves.voting.core.exceptions.AssociateUnableToVoteException;
import br.com.miguelalves.voting.core.exceptions.InvalidCpfException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionClosedException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionNotFoundException;
import br.com.miguelalves.voting.external.eligibility.AssociateEligibilityClient;
import br.com.miguelalves.voting.external.eligibility.VotingEligibility;
import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.vote.domain.Vote;
import br.com.miguelalves.voting.vote.domain.VoteChoice;
import br.com.miguelalves.voting.vote.dto.CreateVoteRequest;
import br.com.miguelalves.voting.vote.dto.VoteResponse;
import br.com.miguelalves.voting.vote.mapper.VoteMapper;
import br.com.miguelalves.voting.vote.repository.VoteRepository;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@ExtendWith(MockitoExtension.class)
class VoteServiceTest {

        private static final Long VOTING_SESSION_ID = 1L;
        private static final Long ASSOCIATE_ID = 2L;
        private static final String CPF = "52998224725";

        @Mock
        private VotingSessionRepository votingSessionRepository;

        @Mock
        private AssociateRepository associateRepository;

        @Mock
        private VoteRepository voteRepository;

        @Mock
        private AssociateEligibilityClient associateEligibilityClient;

        @Mock
        private VoteMapper voteMapper;

        private VoteService voteService;

        @BeforeEach
        void setUp() {
                voteService = new VoteService(
                                votingSessionRepository,
                                associateRepository,
                                voteRepository,
                                associateEligibilityClient,
                                voteMapper);
        }

        @Test
        void shouldRegisterVote() {
                var votingSession = createOpenVotingSession();
                var associate = createAssociate();
                var request = createRequest();
                var savedVote = new Vote(
                                votingSession,
                                associate,
                                request.choice(),
                                LocalDateTime.now());
                var expectedResponse = new VoteResponse(
                                1L,
                                VOTING_SESSION_ID,
                                ASSOCIATE_ID,
                                VoteChoice.YES,
                                savedVote.createdAt());

                prepareEligibleAssociate(votingSession, associate);
                when(voteRepository.save(any(Vote.class))).thenReturn(savedVote);
                when(voteMapper.toResponse(savedVote)).thenReturn(expectedResponse);

                var result = voteService.vote(VOTING_SESSION_ID, request);

                assertThat(result).isEqualTo(expectedResponse);
        }

        @Test
        void shouldThrowWhenVotingSessionDoesNotExist() {
                when(votingSessionRepository.findById(VOTING_SESSION_ID))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> voteService.vote(
                                VOTING_SESSION_ID,
                                createRequest()))
                                .isInstanceOf(VotingSessionNotFoundException.class)
                                .hasMessage("Voting session with ID 1 was not found");
        }

        @Test
        void shouldThrowWhenVotingSessionIsClosed() {
                when(votingSessionRepository.findById(VOTING_SESSION_ID))
                                .thenReturn(Optional.of(createClosedVotingSession()));

                assertThatThrownBy(() -> voteService.vote(
                                VOTING_SESSION_ID,
                                createRequest()))
                                .isInstanceOf(VotingSessionClosedException.class)
                                .hasMessage("Voting session 1 is closed");
        }

        @Test
        void shouldThrowWhenAssociateDoesNotExist() {
                var votingSession = createOpenVotingSession();
                when(votingSessionRepository.findById(VOTING_SESSION_ID))
                                .thenReturn(Optional.of(votingSession));
                when(associateRepository.findById(ASSOCIATE_ID))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> voteService.vote(
                                VOTING_SESSION_ID,
                                createRequest()))
                                .isInstanceOf(AssociateNotFoundException.class)
                                .hasMessage("Associate with ID 2 was not found");
        }

        @Test
        void shouldThrowWhenAssociateAlreadyVoted() {
                var votingSession = createOpenVotingSession();
                var associate = createAssociate();
                when(votingSessionRepository.findById(VOTING_SESSION_ID))
                                .thenReturn(Optional.of(votingSession));
                when(associateRepository.findById(ASSOCIATE_ID))
                                .thenReturn(Optional.of(associate));
                when(voteRepository.existsByVotingSession_IdAndAssociate_Id(
                                VOTING_SESSION_ID,
                                ASSOCIATE_ID))
                                .thenReturn(true);

                assertThatThrownBy(() -> voteService.vote(
                                VOTING_SESSION_ID,
                                createRequest()))
                                .isInstanceOf(AssociateAlreadyVotedException.class)
                                .hasMessage("Associate 2 has already voted in voting session 1");
        }

        @Test
        void shouldThrowWhenAssociateIsUnableToVote() {
                var votingSession = createOpenVotingSession();
                var associate = createAssociate();
                prepareVotingSessionAndAssociate(votingSession, associate);
                when(associateEligibilityClient.check(CPF))
                                .thenReturn(VotingEligibility.UNABLE_TO_VOTE);

                assertThatThrownBy(() -> voteService.vote(
                                VOTING_SESSION_ID,
                                createRequest()))
                                .isInstanceOf(AssociateUnableToVoteException.class)
                                .hasMessage("Associate with CPF 52998224725 is unable to vote");
        }

        @Test
        void shouldPropagateInvalidCpfException() {
                var votingSession = createOpenVotingSession();
                var associate = createAssociate();
                prepareVotingSessionAndAssociate(votingSession, associate);
                when(associateEligibilityClient.check(CPF))
                                .thenThrow(new InvalidCpfException(CPF));

                assertThatThrownBy(() -> voteService.vote(
                                VOTING_SESSION_ID,
                                createRequest()))
                                .isInstanceOf(InvalidCpfException.class)
                                .hasMessage("CPF 52998224725 is invalid");
        }

        private void prepareEligibleAssociate(
                        VotingSession votingSession,
                        Associate associate) {
                prepareVotingSessionAndAssociate(votingSession, associate);
                when(associateEligibilityClient.check(CPF))
                                .thenReturn(VotingEligibility.ABLE_TO_VOTE);
        }

        private void prepareVotingSessionAndAssociate(
                        VotingSession votingSession,
                        Associate associate) {
                when(votingSessionRepository.findById(VOTING_SESSION_ID))
                                .thenReturn(Optional.of(votingSession));
                when(associateRepository.findById(ASSOCIATE_ID))
                                .thenReturn(Optional.of(associate));
        }

        private CreateVoteRequest createRequest() {
                return new CreateVoteRequest(ASSOCIATE_ID, VoteChoice.YES);
        }

        private Associate createAssociate() {
                return new Associate(CPF);
        }

        private VotingSession createOpenVotingSession() {
                return new VotingSession(
                                createProposal(),
                                Duration.ofHours(1),
                                LocalDateTime.now().minusMinutes(1));
        }

        private VotingSession createClosedVotingSession() {
                return new VotingSession(
                                createProposal(),
                                Duration.ofMinutes(1),
                                LocalDateTime.now().minusHours(1));
        }

        private Proposal createProposal() {
                return new Proposal(
                                "Annual budget approval",
                                "Voting for approval of the annual budget");
        }
}
