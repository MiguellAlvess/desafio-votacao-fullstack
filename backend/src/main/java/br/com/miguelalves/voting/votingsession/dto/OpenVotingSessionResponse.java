package br.com.miguelalves.voting.votingsession.dto;

import java.time.LocalDateTime;

public record OpenVotingSessionResponse(
        Long id,
        Long proposalId,
        String proposalTitle,
        LocalDateTime startsAt,
        LocalDateTime endsAt) {
}
