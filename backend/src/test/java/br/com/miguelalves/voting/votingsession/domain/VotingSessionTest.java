package br.com.miguelalves.voting.votingsession.domain;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import br.com.miguelalves.voting.proposal.domain.Proposal;

class VotingSessionTest {

    private final LocalDateTime startsAt = LocalDateTime.of(2026, 9, 14, 14, 0);

    @Test
    void shouldCreateVotingSessionWithGivenDuration() {
        var proposal = createProposal();

        var votingSession = new VotingSession(
                proposal,
                Duration.ofMinutes(5),
                startsAt);

        assertEquals(proposal, votingSession.proposal());
        assertEquals(startsAt, votingSession.startsAt());
        assertEquals(
                startsAt.plusMinutes(5),
                votingSession.endsAt());
    }

    @Test
    void shouldUseDefaultDurationWhenDurationIsNull() {
        var proposal = createProposal();

        var votingSession = new VotingSession(
                proposal,
                null,
                startsAt);

        assertEquals(
                startsAt.plusMinutes(1),
                votingSession.endsAt());
    }

    @Test
    void shouldThrowExceptionWhenProposalIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VotingSession(
                        null,
                        Duration.ofMinutes(5),
                        startsAt));

        assertEquals(
                "Proposal cannot be null",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsNull() {
        var proposal = createProposal();

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VotingSession(
                        proposal,
                        Duration.ofMinutes(5),
                        null));

        assertEquals(
                "Start time cannot be null",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDurationIsZero() {
        var proposal = createProposal();

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VotingSession(
                        proposal,
                        Duration.ZERO,
                        startsAt));

        assertEquals(
                "Voting session duration must be greater than zero",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDurationIsNegative() {
        var proposal = createProposal();

        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new VotingSession(
                        proposal,
                        Duration.ofMinutes(-1),
                        startsAt));

        assertEquals(
                "Voting session duration must be greater than zero",
                exception.getMessage());
    }

    @Test
    void shouldBeOpenAtStartTime() {
        var votingSession = createVotingSession();

        assertTrue(votingSession.isOpenAt(startsAt));
    }

    @Test
    void shouldBeOpenBeforeEndTime() {
        var votingSession = createVotingSession();

        assertTrue(
                votingSession.isOpenAt(
                        startsAt.plusMinutes(4)));
    }

    @Test
    void shouldNotBeOpenBeforeStartTime() {
        var votingSession = createVotingSession();

        assertFalse(
                votingSession.isOpenAt(
                        startsAt.minusSeconds(1)));
    }

    @Test
    void shouldNotBeOpenAtEndTime() {
        var votingSession = createVotingSession();

        assertFalse(
                votingSession.isOpenAt(
                        startsAt.plusMinutes(5)));
    }

    private Proposal createProposal() {
        return new Proposal(
                "Annual budget approval",
                "Voting for approval of the annual budget");
    }

    private VotingSession createVotingSession() {
        return new VotingSession(
                createProposal(),
                Duration.ofMinutes(5),
                startsAt);
    }
}
