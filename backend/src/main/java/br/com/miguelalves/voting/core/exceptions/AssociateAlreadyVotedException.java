package br.com.miguelalves.voting.core.exceptions;

public class AssociateAlreadyVotedException extends RuntimeException {

    public AssociateAlreadyVotedException(
            Long associateId,
            Long votingSessionId) {
        super("Associate " + associateId
                + " has already voted in voting session " + votingSessionId);
    }
}
