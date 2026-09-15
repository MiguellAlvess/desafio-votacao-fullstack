package br.com.miguelalves.voting.proposal.mapper;

import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.proposal.dto.ProposalResponse;

@Component
public class ProposalMapper {

    public ProposalResponse toResponse(Proposal proposal) {
        return new ProposalResponse(
                proposal.id(),
                proposal.title(),
                proposal.description(),
                proposal.createdAt());
    }
}
