package br.com.miguelalves.voting.proposal.repository;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProposalRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private VotingSessionRepository votingSessionRepository;

    @Test
    void shouldSaveProposal() {
        var proposal = new Proposal(
                "Annual budget approval",
                "Voting for approval of the annual budget");

        var savedProposal = proposalRepository.save(proposal);

        assertThat(savedProposal.id()).isNotNull();
        assertThat(savedProposal.title())
                .isEqualTo("Annual budget approval");
        assertThat(savedProposal.description())
                .isEqualTo("Voting for approval of the annual budget");
        assertThat(savedProposal.createdAt()).isNotNull();
    }

    @Test
    void shouldFindProposalById() {
        var proposal = new Proposal(
                "Annual budget approval",
                "Voting for approval of the annual budget");

        var savedProposal = proposalRepository.save(proposal);
        var foundProposal = proposalRepository.findById(savedProposal.id());

        assertThat(foundProposal).isPresent();
        assertThat(foundProposal.get().title())
                .isEqualTo("Annual budget approval");
    }

    @Test
    void shouldReturnProposalWithoutSession() {
        var proposal = proposalRepository.save(new Proposal("Proposal without session", null));

        var result = proposalRepository.findAllWithSession();

        assertThat(result).singleElement().satisfies(item -> {
            assertThat(item.proposal().id()).isEqualTo(proposal.id());
            assertThat(item.session()).isNull();
        });
    }

    @Test
    void shouldReturnProposalWithSession() {
        var proposal = proposalRepository.save(new Proposal("Proposal with session", null));
        var session = votingSessionRepository.save(new VotingSession(
                proposal,
                Duration.ofMinutes(5),
                LocalDateTime.of(2026, 9, 21, 10, 0)));

        var result = proposalRepository.findAllWithSession();

        assertThat(result).singleElement().satisfies(item -> {
            assertThat(item.proposal().id()).isEqualTo(proposal.id());
            assertThat(item.session().id()).isEqualTo(session.id());
            assertThat(item.session().proposal().id()).isEqualTo(proposal.id());
        });
    }

    @Test
    void shouldReturnMultipleProposalsWithTheirSessions() {
        var withoutSession = proposalRepository.save(new Proposal("Without session", null));
        var withSession = proposalRepository.save(new Proposal("With session", null));
        var session = votingSessionRepository.save(new VotingSession(
                withSession,
                Duration.ofMinutes(5),
                LocalDateTime.of(2026, 9, 21, 10, 0)));

        var result = proposalRepository.findAllWithSession();

        assertThat(result).hasSize(2);
        assertThat(result).anySatisfy(item -> {
            assertThat(item.proposal().id()).isEqualTo(withoutSession.id());
            assertThat(item.session()).isNull();
        });
        assertThat(result).anySatisfy(item -> {
            assertThat(item.proposal().id()).isEqualTo(withSession.id());
            assertThat(item.session().id()).isEqualTo(session.id());
        });
    }
}
