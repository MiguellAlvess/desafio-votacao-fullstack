package br.com.miguelalves.voting.core.exceptions;

public class VotingSessionAlreadyExistsException extends RuntimeException {

    public VotingSessionAlreadyExistsException(Long proposalId) {
        super("A voting session already exists for proposal " + proposalId);
    }
}
