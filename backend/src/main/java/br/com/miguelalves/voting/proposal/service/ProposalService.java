package br.com.miguelalves.voting.proposal.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.proposal.dto.CreateProposalRequest;
import br.com.miguelalves.voting.proposal.dto.ProposalResponse;
import br.com.miguelalves.voting.proposal.mapper.ProposalMapper;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;

@Service
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final ProposalMapper proposalMapper;

    public ProposalService(
            ProposalRepository proposalRepository,
            ProposalMapper proposalMapper) {
        this.proposalRepository = proposalRepository;
        this.proposalMapper = proposalMapper;
    }

    @Transactional
    public ProposalResponse create(CreateProposalRequest request) {
        var proposal = new Proposal(
                request.title(),
                request.description());
        var savedProposal = proposalRepository.save(proposal);
        return proposalMapper.toResponse(savedProposal);
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse> findAll() {
        return proposalRepository.findAll()
                .stream()
                .map(proposalMapper::toResponse)
                .toList();
    }
}
