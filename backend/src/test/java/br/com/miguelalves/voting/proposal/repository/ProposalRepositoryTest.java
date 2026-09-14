package br.com.miguelalves.voting.proposal.repository;

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

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProposalRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private ProposalRepository proposalRepository;

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
}