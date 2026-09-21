package br.com.miguelalves.voting.proposal.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.proposal.dto.CreateProposalRequest;
import br.com.miguelalves.voting.proposal.dto.ProposalResponse;
import br.com.miguelalves.voting.proposal.mapper.ProposalMapper;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;
import br.com.miguelalves.voting.proposal.repository.ProposalWithSession;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;

@ExtendWith(MockitoExtension.class)
class ProposalServiceTest {

        @Mock
        private ProposalRepository proposalRepository;

        private ProposalService proposalService;

        @BeforeEach
        void setUp() {
                proposalService = new ProposalService(
                                proposalRepository,
                                new ProposalMapper());
        }

        @Test
        void shouldCreateProposal() {
                var request = new CreateProposalRequest(
                                "Annual budget approval",
                                "Voting for approval of the annual budget");
                var savedProposal = new Proposal(
                                request.title(),
                                request.description());
                var expectedResponse = new ProposalResponse(
                                1L,
                                savedProposal.title(),
                                savedProposal.description(),
                                savedProposal.createdAt());

                when(proposalRepository.save(any(Proposal.class)))
                                .thenReturn(savedProposal);
                var result = proposalService.create(request);

                assertThat(result.title()).isEqualTo(expectedResponse.title());
                assertThat(result.description()).isEqualTo(expectedResponse.description());
                assertThat(result.createdAt()).isEqualTo(expectedResponse.createdAt());
        }

        @Test
        void shouldListProposals() {
                var firstProposal = new Proposal(
                                "Annual budget approval",
                                "Voting for approval of the annual budget");
                var secondProposal = new Proposal(
                                "Board election",
                                null);
                var firstResponse = new ProposalResponse(
                                1L,
                                firstProposal.title(),
                                firstProposal.description(),
                                LocalDateTime.now());
                var secondResponse = new ProposalResponse(
                                2L,
                                secondProposal.title(),
                                secondProposal.description(),
                                LocalDateTime.now());

                when(proposalRepository.findAll())
                                .thenReturn(List.of(firstProposal, secondProposal));
                var result = proposalService.findAll();

                assertThat(result).extracting(ProposalResponse::title)
                                .containsExactly(firstResponse.title(), secondResponse.title());
        }

        @Test
        void shouldReturnProposalWithoutSession() {
                var proposal = new Proposal("Without session", null);
                when(proposalRepository.findAllWithSession())
                                .thenReturn(List.of(new ProposalWithSession(proposal, null)));

                var result = proposalService.findAllForManagement();

                assertThat(result).singleElement().satisfies(response -> {
                        assertThat(response.title()).isEqualTo("Without session");
                        assertThat(response.session()).isNull();
                });
        }

        @Test
        void shouldReturnProposalWithOpenSession() {
                var proposal = new Proposal("Open session", null);
                var session = new VotingSession(proposal, Duration.ofMinutes(5), LocalDateTime.now());
                when(proposalRepository.findAllWithSession())
                                .thenReturn(List.of(new ProposalWithSession(proposal, session)));

                var result = proposalService.findAllForManagement();

                assertThat(result.getFirst().session().status()).isEqualTo(VotingSessionStatus.OPEN);
        }

        @Test
        void shouldReturnProposalWithClosedSession() {
                var proposal = new Proposal("Closed session", null);
                var session = new VotingSession(
                                proposal,
                                Duration.ofMinutes(1),
                                LocalDateTime.now().minusMinutes(2));
                when(proposalRepository.findAllWithSession())
                                .thenReturn(List.of(new ProposalWithSession(proposal, session)));

                var result = proposalService.findAllForManagement();

                assertThat(result.getFirst().session().status()).isEqualTo(VotingSessionStatus.CLOSED);
        }

        @Test
        void shouldReturnAllProposalsForManagement() {
                var withoutSession = new Proposal("Without session", null);
                var openProposal = new Proposal("Open session", null);
                var closedProposal = new Proposal("Closed session", null);
                var openSession = new VotingSession(
                                openProposal,
                                Duration.ofMinutes(5),
                                LocalDateTime.now());
                var closedSession = new VotingSession(
                                closedProposal,
                                Duration.ofMinutes(1),
                                LocalDateTime.now().minusMinutes(2));
                when(proposalRepository.findAllWithSession()).thenReturn(List.of(
                                new ProposalWithSession(withoutSession, null),
                                new ProposalWithSession(openProposal, openSession),
                                new ProposalWithSession(closedProposal, closedSession)));

                var result = proposalService.findAllForManagement();

                assertThat(result).hasSize(3);
                assertThat(result.get(0).session()).isNull();
                assertThat(result.get(1).session().status()).isEqualTo(VotingSessionStatus.OPEN);
                assertThat(result.get(2).session().status()).isEqualTo(VotingSessionStatus.CLOSED);
        }
}
