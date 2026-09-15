package br.com.miguelalves.voting.core.exceptions;

public class ProposalNotFoundException extends RuntimeException {

    public ProposalNotFoundException(Long proposalId) {
        super("Proposal with ID " + proposalId + " was not found");
    }
}
