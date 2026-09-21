package br.com.miguelalves.voting.votingsession.dto;

import java.time.LocalDateTime;

import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;

public record VotingSessionDetailsResponse(
        Long id,
        Long proposalId,
        String proposalTitle,
        String proposalDescription,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        VotingSessionStatus status) {
}
