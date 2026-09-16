package br.com.miguelalves.voting.core.exceptions;

public class VotingSessionClosedException extends RuntimeException {

    public VotingSessionClosedException(Long votingSessionId) {
        super("Voting session " + votingSessionId + " is closed");
    }
}
