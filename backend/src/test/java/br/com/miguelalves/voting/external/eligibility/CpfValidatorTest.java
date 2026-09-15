package br.com.miguelalves.voting.external.eligibility;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CpfValidatorTest {

    private final CpfValidator cpfValidator = new CpfValidator();

    @Test
    void shouldReturnTrueForValidCpf() {
        assertThat(cpfValidator.isValid("52998224725")).isTrue();
    }

    @Test
    void shouldReturnFalseForCpfWithInvalidCheckDigits() {
        assertThat(cpfValidator.isValid("52998224724")).isFalse();
    }

    @Test
    void shouldReturnFalseForCpfWithRepeatedDigits() {
        assertThat(cpfValidator.isValid("11111111111")).isFalse();
    }

    @Test
    void shouldReturnFalseForNullCpf() {
        assertThat(cpfValidator.isValid(null)).isFalse();
    }

    @Test
    void shouldReturnFalseForBlankCpf() {
        assertThat(cpfValidator.isValid("   ")).isFalse();
    }
}
