package br.com.miguelalves.voting.core.exceptions;

public class AssociateNotFoundException extends RuntimeException {

    public AssociateNotFoundException(Long associateId) {
        super("Associate with ID " + associateId + " was not found");
    }
}
