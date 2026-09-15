package br.com.miguelalves.voting.proposal.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProposalRequest(
        @NotBlank(message = "Proposal title cannot be blank") String title,
        String description) {
}
