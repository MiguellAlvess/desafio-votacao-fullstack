package br.com.miguelalves.voting.core.exceptions;

public class InvalidCpfException extends RuntimeException {

    public InvalidCpfException(String cpf) {
        super("CPF " + cpf + " is invalid");
    }
}
