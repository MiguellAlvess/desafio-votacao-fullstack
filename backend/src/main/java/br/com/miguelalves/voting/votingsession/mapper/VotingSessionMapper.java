package br.com.miguelalves.voting.votingsession.mapper;

import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionResponse;
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
}
