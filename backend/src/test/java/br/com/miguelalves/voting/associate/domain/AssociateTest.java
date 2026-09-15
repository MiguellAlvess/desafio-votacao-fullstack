package br.com.miguelalves.voting.associate.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class AssociateTest {

    @Test
    void shouldCreateAssociateWithValidCpf() {
        var associate = new Associate("12345678901");

        assertEquals("12345678901", associate.cpf());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Associate(null));

        assertEquals(
                "Associate CPF cannot be blank",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCpfIsBlank() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Associate("   "));

        assertEquals(
                "Associate CPF cannot be blank",
                exception.getMessage());
    }
}
