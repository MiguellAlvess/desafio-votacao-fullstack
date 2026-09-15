package br.com.miguelalves.voting.proposal.service;

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

@ExtendWith(MockitoExtension.class)
class ProposalServiceTest {

        @Mock
        private ProposalRepository proposalRepository;

        @Mock
        private ProposalMapper proposalMapper;

        private ProposalService proposalService;

        @BeforeEach
        void setUp() {
                proposalService = new ProposalService(
                                proposalRepository,
                                proposalMapper);
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
                when(proposalMapper.toResponse(savedProposal))
                                .thenReturn(expectedResponse);
                var result = proposalService.create(request);

                assertThat(result).isEqualTo(expectedResponse);
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
                when(proposalMapper.toResponse(firstProposal))
                                .thenReturn(firstResponse);
                when(proposalMapper.toResponse(secondProposal))
                                .thenReturn(secondResponse);

                var result = proposalService.findAll();

                assertThat(result).containsExactly(firstResponse, secondResponse);
        }
}
