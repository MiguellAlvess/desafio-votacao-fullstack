package br.com.miguelalves.voting.proposal.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.proposal.dto.ProposalManagementResponse;
import br.com.miguelalves.voting.proposal.dto.ProposalResponse;
import br.com.miguelalves.voting.proposal.repository.ProposalWithSession;
import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionSummaryResponse;

@Component
public class ProposalMapper {

    public ProposalResponse toResponse(Proposal proposal) {
        return new ProposalResponse(
                proposal.id(),
                proposal.title(),
                proposal.description(),
                proposal.createdAt());
    }

    public ProposalManagementResponse toManagementResponse(
            ProposalWithSession proposalWithSession,
            LocalDateTime now) {
        var proposal = proposalWithSession.proposal();
        var session = proposalWithSession.session();
        var sessionResponse = session == null
                ? null
                : new VotingSessionSummaryResponse(
                        session.id(),
                        session.isOpenAt(now)
                                ? VotingSessionStatus.OPEN
                                : VotingSessionStatus.CLOSED,
                        session.startsAt(),
                        session.endsAt());
        return new ProposalManagementResponse(
                proposal.id(),
                proposal.title(),
                proposal.description(),
                proposal.createdAt(),
                sessionResponse);
    }
}
