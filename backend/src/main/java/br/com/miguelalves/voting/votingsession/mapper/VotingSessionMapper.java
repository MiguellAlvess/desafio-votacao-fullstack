package br.com.miguelalves.voting.votingsession.mapper;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionResponse;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionDetailsResponse;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;

@Component
public class VotingSessionMapper {

    public VotingSessionResponse toResponse(VotingSession votingSession) {
        return new VotingSessionResponse(
                votingSession.id(),
                votingSession.proposal().id(),
                votingSession.startsAt(),
                votingSession.endsAt());
    }

    public OpenVotingSessionResponse toOpenResponse(VotingSession votingSession) {
        return new OpenVotingSessionResponse(
                votingSession.id(),
                votingSession.proposal().id(),
                votingSession.proposal().title(),
                votingSession.startsAt(),
                votingSession.endsAt());
    }

    public VotingSessionDetailsResponse toDetailsResponse(
            VotingSession votingSession,
            LocalDateTime now) {
        var proposal = votingSession.proposal();
        return new VotingSessionDetailsResponse(
                votingSession.id(),
                proposal.id(),
                proposal.title(),
                proposal.description(),
                votingSession.startsAt(),
                votingSession.endsAt(),
                votingSession.isOpenAt(now)
                        ? VotingSessionStatus.OPEN
                        : VotingSessionStatus.CLOSED);
    }
}
