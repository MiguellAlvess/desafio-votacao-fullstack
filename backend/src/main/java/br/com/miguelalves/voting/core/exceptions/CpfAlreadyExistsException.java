package br.com.miguelalves.voting.core.exceptions;

public class CpfAlreadyExistsException extends RuntimeException {

    public CpfAlreadyExistsException(String cpf) {
        super("An associate with CPF " + cpf + " already exists");
    }
}