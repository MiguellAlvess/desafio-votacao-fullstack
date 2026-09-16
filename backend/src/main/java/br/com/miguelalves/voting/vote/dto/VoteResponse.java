package br.com.miguelalves.voting.vote.dto;

import java.time.LocalDateTime;

import br.com.miguelalves.voting.vote.domain.VoteChoice;

public record VoteResponse(
        Long id,
        Long votingSessionId,
        Long associateId,
        VoteChoice choice,
        LocalDateTime createdAt) {
}
