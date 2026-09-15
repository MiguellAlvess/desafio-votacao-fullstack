package br.com.miguelalves.voting.core.exceptions;

public class AssociateUnableToVoteException extends RuntimeException {

    public AssociateUnableToVoteException(String cpf) {
        super("Associate with CPF " + cpf + " is unable to vote");
    }
}
