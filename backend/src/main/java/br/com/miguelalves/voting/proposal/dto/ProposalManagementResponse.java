package br.com.miguelalves.voting.proposal.dto;

import java.time.LocalDateTime;

import br.com.miguelalves.voting.votingsession.dto.VotingSessionSummaryResponse;

public record ProposalManagementResponse(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt,
        VotingSessionSummaryResponse session) {
}
