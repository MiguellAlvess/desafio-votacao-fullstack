package br.com.miguelalves.voting.proposal.repository;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;

public record ProposalWithSession(
        Proposal proposal,
        VotingSession session) {
}
