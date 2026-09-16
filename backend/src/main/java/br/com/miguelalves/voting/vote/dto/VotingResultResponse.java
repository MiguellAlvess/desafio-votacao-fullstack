package br.com.miguelalves.voting.vote.dto;

public record VotingResultResponse(
        Long votingSessionId,
        long yesVotes,
        long noVotes,
        long totalVotes) {
}
