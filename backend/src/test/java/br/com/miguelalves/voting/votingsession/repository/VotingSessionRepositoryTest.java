package br.com.miguelalves.voting.votingsession.repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 9, 16, 10, 0);

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

    @Test
    void shouldReturnSessionThatIsOpen() {
        var openSession = saveSession("Open proposal", NOW, Duration.ofMinutes(5));

        var sessions = findOpenSessions();

        assertThat(sessions).extracting(VotingSession::id)
                .containsExactly(openSession.id());
    }

    @Test
    void shouldNotReturnSessionThatHasNotStarted() {
        saveSession("Future proposal", NOW.plusSeconds(1), Duration.ofMinutes(5));

        assertThat(findOpenSessions()).isEmpty();
    }

    @Test
    void shouldNotReturnSessionThatHasEnded() {
        saveSession("Ended proposal", NOW.minusMinutes(5), Duration.ofMinutes(5));

        assertThat(findOpenSessions()).isEmpty();
    }

    @Test
    void shouldReturnMultipleOpenSessions() {
        var first = saveSession("First proposal", NOW.minusMinutes(2), Duration.ofMinutes(5));
        var second = saveSession("Second proposal", NOW, Duration.ofMinutes(5));
        saveSession("Future proposal", NOW.plusMinutes(1), Duration.ofMinutes(5));

        assertThat(findOpenSessions()).extracting(VotingSession::id)
                .containsExactlyInAnyOrder(first.id(), second.id());
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoSessions() {
        assertThat(findOpenSessions()).isEmpty();
    }

    private List<VotingSession> findOpenSessions() {
        return votingSessionRepository.findAllByStartsAtLessThanEqualAndEndsAtAfter(
                NOW,
                NOW);
    }

    private VotingSession saveSession(
            String proposalTitle,
            LocalDateTime startsAt,
            Duration duration) {
        var proposal = proposalRepository.save(new Proposal(proposalTitle, null));
        return votingSessionRepository.save(new VotingSession(proposal, duration, startsAt));
    }
}
