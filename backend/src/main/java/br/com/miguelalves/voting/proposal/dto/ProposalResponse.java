package br.com.miguelalves.voting.proposal.dto;

import java.time.LocalDateTime;

public record ProposalResponse(
        Long id,
        String title,
        String description,
        LocalDateTime createdAt) {
}
