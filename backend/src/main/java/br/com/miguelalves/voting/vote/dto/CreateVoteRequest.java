package br.com.miguelalves.voting.vote.dto;

import br.com.miguelalves.voting.vote.domain.VoteChoice;
import jakarta.validation.constraints.NotNull;

public record CreateVoteRequest(
        @NotNull(message = "Associate ID cannot be null") Long associateId,
        @NotNull(message = "Vote choice cannot be null") VoteChoice choice) {
}
