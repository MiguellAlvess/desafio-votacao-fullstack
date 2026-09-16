package br.com.miguelalves.voting.core.exceptions;

public class VotingSessionNotFoundException extends RuntimeException {

    public VotingSessionNotFoundException(Long votingSessionId) {
        super("Voting session with ID " + votingSessionId + " was not found");
    }
}
