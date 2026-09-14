package br.com.miguelalves.voting.proposal.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ProposalTest {

    @Test
    void shouldCreateProposalWithValidData() {
        var proposal = new Proposal(
                "Annual budget approval",
                "Voting for approval of the annual budget");

        assertEquals("Annual budget approval", proposal.title());
        assertEquals(
                "Voting for approval of the annual budget",
                proposal.description());
        assertNotNull(proposal.createdAt());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Proposal(null, "Description"));

        assertEquals(
                "Proposal title cannot be blank",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenTitleIsBlank() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Proposal("   ", "Description"));

        assertEquals(
                "Proposal title cannot be blank",
                exception.getMessage());
    }
}