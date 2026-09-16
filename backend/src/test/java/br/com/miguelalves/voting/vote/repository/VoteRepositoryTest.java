package br.com.miguelalves.voting.vote.repository;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.com.miguelalves.voting.associate.domain.Associate;
import br.com.miguelalves.voting.associate.repository.AssociateRepository;
import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;
import br.com.miguelalves.voting.vote.domain.Vote;
import br.com.miguelalves.voting.vote.domain.VoteChoice;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@Testcontainers
@DataJpaTest
class VoteRepositoryTest {

        @Container
        @ServiceConnection
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

        @Autowired
        private VoteRepository voteRepository;

        @Autowired
        private VotingSessionRepository votingSessionRepository;

        @Autowired
        private ProposalRepository proposalRepository;

        @Autowired
        private AssociateRepository associateRepository;

        @Test
        void shouldSaveVote() {
                var votingSession = createAndSaveVotingSession();
                var associate = createAndSaveAssociate();
                var createdAt = LocalDateTime.of(
                                2026, 9, 14, 14, 2);
                var vote = new Vote(
                                votingSession,
                                associate,
                                VoteChoice.YES,
                                createdAt);

                var savedVote = voteRepository.save(vote);

                assertThat(savedVote.id())
                                .isNotNull();
                assertThat(
                                savedVote.votingSession().id())
                                .isEqualTo(votingSession.id());
                assertThat(
                                savedVote.associate().id())
                                .isEqualTo(associate.id());
                assertThat(savedVote.choice())
                                .isEqualTo(VoteChoice.YES);
                assertThat(savedVote.createdAt())
                                .isEqualTo(createdAt);
        }

        @Test
        void shouldFindVoteById() {
                var votingSession = createAndSaveVotingSession();
                var associate = createAndSaveAssociate();

                var vote = voteRepository.save(
                                new Vote(
                                                votingSession,
                                                associate,
                                                VoteChoice.NO,
                                                LocalDateTime.of(
                                                                2026, 9, 14, 14, 2)));
                var foundVote = voteRepository.findById(
                                vote.id());

                assertThat(foundVote).isPresent();
                assertThat(
                                foundVote.get().associate().id())
                                .isEqualTo(associate.id());
                assertThat(
                                foundVote.get().choice())
                                .isEqualTo(VoteChoice.NO);
        }

        @Test
        void shouldReturnTrueWhenAssociateAlreadyVotedInSession() {
                var votingSession = createAndSaveVotingSession();
                var associate = createAndSaveAssociate();
                voteRepository.save(
                                new Vote(
                                                votingSession,
                                                associate,
                                                VoteChoice.YES,
                                                LocalDateTime.of(
                                                                2026, 9, 14, 14, 2)));

                var exists = voteRepository
                                .existsByVotingSession_IdAndAssociate_Id(
                                                votingSession.id(),
                                                associate.id());

                assertThat(exists).isTrue();
        }

        @Test
        void shouldReturnFalseWhenAssociateHasNotVotedInSession() {
                var votingSession = createAndSaveVotingSession();

                var associate = createAndSaveAssociate();
                var exists = voteRepository
                                .existsByVotingSession_IdAndAssociate_Id(
                                                votingSession.id(),
                                                associate.id());

                assertThat(exists).isFalse();
        }

        @Test
        void shouldCountYesAndNoVotes() {
                var session = createAndSaveVotingSession();
                saveVote(session, "52998224725", VoteChoice.YES);
                saveVote(session, "11144477735", VoteChoice.YES);
                saveVote(session, "12345678909", VoteChoice.YES);
                saveVote(session, "98765432100", VoteChoice.NO);
                saveVote(session, "16899535009", VoteChoice.NO);

                var count = voteRepository.countByVotingSessionId(session.id());

                assertThat(count.getYesVotes()).isEqualTo(3);
                assertThat(count.getNoVotes()).isEqualTo(2);
        }

        @Test
        void shouldReturnZeroCountsWhenSessionHasNoVotes() {
                var session = createAndSaveVotingSession();

                var count = voteRepository.countByVotingSessionId(session.id());

                assertThat(count.getYesVotes()).isZero();
                assertThat(count.getNoVotes()).isZero();
        }

        @Test
        void shouldNotCountVotesFromAnotherSession() {
                var session = createAndSaveVotingSession();
                var otherProposal = proposalRepository.save(new Proposal("Other proposal", null));
                var otherSession = votingSessionRepository.save(new VotingSession(
                                otherProposal,
                                Duration.ofMinutes(5),
                                LocalDateTime.of(2026, 9, 14, 14, 0)));
                saveVote(session, "52998224725", VoteChoice.YES);
                saveVote(otherSession, "11144477735", VoteChoice.NO);

                var count = voteRepository.countByVotingSessionId(session.id());

                assertThat(count.getYesVotes()).isEqualTo(1);
                assertThat(count.getNoVotes()).isZero();
        }

        private void saveVote(VotingSession session, String cpf, VoteChoice choice) {
                var associate = associateRepository.save(new Associate(cpf));
                voteRepository.save(new Vote(
                                session,
                                associate,
                                choice,
                                LocalDateTime.of(2026, 9, 14, 14, 2)));
        }

        private Associate createAndSaveAssociate() {
                return associateRepository.save(
                                new Associate("12345678901"));
        }

        private VotingSession createAndSaveVotingSession() {
                var proposal = proposalRepository.save(
                                new Proposal(
                                                "Annual budget approval",
                                                "Voting for approval of the annual budget"));

                return votingSessionRepository.save(
                                new VotingSession(
                                                proposal,
                                                Duration.ofMinutes(5),
                                                LocalDateTime.of(
                                                                2026, 9, 14, 14, 0)));
        }
}
