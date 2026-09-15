package br.com.miguelalves.voting.votingsession.dto;

import java.time.LocalDateTime;

public record VotingSessionResponse(
        Long id,
        Long proposalId,
        LocalDateTime startsAt,
        LocalDateTime endsAt) {
}
