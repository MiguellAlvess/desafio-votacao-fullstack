package br.com.miguelalves.voting.votingsession.repository;

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
import br.com.miguelalves.voting.votingsession.domain.VotingSession;

@Testcontainers
@DataJpaTest
class VotingSessionRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Test
    void shouldSaveVotingSession() {
        var proposal = proposalRepository.save(
                new Proposal(
                        "Annual budget approval",
                        "Voting for approval of the annual budget"));
        var startsAt = LocalDateTime.of(2026, 9, 14, 14, 0);
        var votingSession = new VotingSession(
                proposal,
                Duration.ofMinutes(5),
                startsAt);

        var savedSession = votingSessionRepository.save(votingSession);

        assertThat(savedSession.id()).isNotNull();
        assertThat(savedSession.proposal().id()).isEqualTo(proposal.id());
        assertThat(savedSession.startsAt()).isEqualTo(startsAt);
        assertThat(savedSession.endsAt()).isEqualTo(startsAt.plusMinutes(5));
    }

    @Test
    void shouldFindVotingSessionById() {
        var proposal = proposalRepository.save(
                new Proposal(
                        "Annual budget approval",
                        "Voting for approval of the annual budget"));

        var votingSession = votingSessionRepository.save(
                new VotingSession(
                        proposal,
                        Duration.ofMinutes(5),
                        LocalDateTime.of(2026, 9, 14, 14, 0)));
        var foundSession = votingSessionRepository.findById(votingSession.id());

        assertThat(foundSession).isPresent();
        assertThat(foundSession.get().proposal().id())
                .isEqualTo(proposal.id());
    }

    @Test
    void shouldCheckWhetherVotingSessionExistsByProposalId() {
        var proposal = proposalRepository.save(
                new Proposal(
                        "Annual budget approval",
                        "Voting for approval of the annual budget"));
        votingSessionRepository.save(
                new VotingSession(
                        proposal,
                        Duration.ofMinutes(5),
                        LocalDateTime.of(2026, 9, 14, 14, 0)));

        var exists = votingSessionRepository.existsByProposalId(proposal.id());

        assertThat(exists).isTrue();
    }
}
