package br.com.miguelalves.voting.external.eligibility;

final class CpfValidator {

    private static final int CPF_LENGTH = 11;

    boolean isValid(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}")) {
            return false;
        }
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }
        var firstDigit = calculateDigit(cpf, 9);
        var secondDigit = calculateDigit(cpf, 10);
        return firstDigit == Character.getNumericValue(cpf.charAt(9))
                && secondDigit == Character.getNumericValue(cpf.charAt(10));
    }

    private int calculateDigit(String cpf, int length) {
        var sum = 0;
        var weight = length + 1;
        for (var i = 0; i < length; i++) {
            var digit = Character.getNumericValue(cpf.charAt(i));

            sum += digit * weight--;
        }
        var remainder = sum % CPF_LENGTH;
        return remainder < 2
                ? 0
                : CPF_LENGTH - remainder;
    }
}