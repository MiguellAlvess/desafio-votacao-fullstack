package br.com.miguelalves.voting.votingsession.dto;

import java.time.LocalDateTime;

import br.com.miguelalves.voting.votingsession.domain.VotingSessionStatus;

public record VotingSessionSummaryResponse(
        Long id,
        VotingSessionStatus status,
        LocalDateTime startsAt,
        LocalDateTime endsAt) {
}
