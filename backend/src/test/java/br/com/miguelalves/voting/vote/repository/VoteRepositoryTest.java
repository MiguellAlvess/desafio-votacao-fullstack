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

    @Test
    void shouldSaveVote() {
        var votingSession = createAndSaveVotingSession();

        var createdAt = LocalDateTime.of(2026, 9, 14, 14, 2);

        var vote = new Vote(
                votingSession,
                "associate-123",
                VoteChoice.YES,
                createdAt);

        var savedVote = voteRepository.save(vote);

        assertThat(savedVote.id()).isNotNull();
        assertThat(savedVote.votingSession().id())
                .isEqualTo(votingSession.id());
        assertThat(savedVote.associateId())
                .isEqualTo("associate-123");
        assertThat(savedVote.choice())
                .isEqualTo(VoteChoice.YES);
        assertThat(savedVote.createdAt())
                .isEqualTo(createdAt);
    }

    @Test
    void shouldFindVoteById() {
        var votingSession = createAndSaveVotingSession();

        var vote = voteRepository.save(
                new Vote(
                        votingSession,
                        "associate-123",
                        VoteChoice.NO,
                        LocalDateTime.of(2026, 9, 14, 14, 2)));

        var foundVote = voteRepository.findById(vote.id());

        assertThat(foundVote).isPresent();
        assertThat(foundVote.get().associateId())
                .isEqualTo("associate-123");
        assertThat(foundVote.get().choice())
                .isEqualTo(VoteChoice.NO);
    }

    @Test
    void shouldReturnTrueWhenAssociateAlreadyVotedInSession() {
        var votingSession = createAndSaveVotingSession();

        voteRepository.save(
                new Vote(
                        votingSession,
                        "associate-123",
                        VoteChoice.YES,
                        LocalDateTime.of(2026, 9, 14, 14, 2)));
        var exists = voteRepository.existsByVotingSessionIdAndAssociateId(
                votingSession.id(),
                "associate-123");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenAssociateHasNotVotedInSession() {
        var votingSession = createAndSaveVotingSession();

        var exists = voteRepository.existsByVotingSessionIdAndAssociateId(
                votingSession.id(),
                "associate-999");

        assertThat(exists).isFalse();
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
                        LocalDateTime.of(2026, 9, 14, 14, 0)));
    }
}
