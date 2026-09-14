package br.com.miguelalves.voting.vote.domain;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;

class VoteTest {

    private final LocalDateTime sessionStartsAt = LocalDateTime.of(2026, 9, 14, 14, 0);

    private final LocalDateTime voteCreatedAt = LocalDateTime.of(2026, 9, 14, 14, 2);

    @Test
    void shouldCreateVoteWithValidData() {
        var votingSession = createVotingSession();

        var vote = new Vote(
                votingSession,
                "associate-123",
                VoteChoice.YES,
                voteCreatedAt);

        assertEquals(votingSession, vote.votingSession());
        assertEquals("associate-123", vote.associateId());
        assertEquals(VoteChoice.YES, vote.choice());
        assertEquals(voteCreatedAt, vote.createdAt());
    }

    @Test
    void shouldTrimAssociateId() {
        var vote = new Vote(
                createVotingSession(),
                "  associate-123  ",
                VoteChoice.NO,
                voteCreatedAt);

        assertEquals("associate-123", vote.associateId());
    }

    @Test
    void shouldThrowExceptionWhenVotingSessionIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        null,
                        "associate-123",
                        VoteChoice.YES,
                        voteCreatedAt));

        assertEquals(
                "Voting session cannot be null",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAssociateIdIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        createVotingSession(),
                        null,
                        VoteChoice.YES,
                        voteCreatedAt));

        assertEquals(
                "Associate id cannot be blank",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenAssociateIdIsBlank() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        createVotingSession(),
                        "   ",
                        VoteChoice.YES,
                        voteCreatedAt));

        assertEquals(
                "Associate id cannot be blank",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenChoiceIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        createVotingSession(),
                        "associate-123",
                        null,
                        voteCreatedAt));

        assertEquals(
                "Vote choice cannot be null",
                exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenCreationTimeIsNull() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Vote(
                        createVotingSession(),
                        "associate-123",
                        VoteChoice.YES,
                        null));

        assertEquals(
                "Creation time cannot be null",
                exception.getMessage());
    }

    private VotingSession createVotingSession() {
        var proposal = new Proposal(
                "Annual budget approval",
                "Voting for approval of the annual budget");

        return new VotingSession(
                proposal,
                Duration.ofMinutes(5),
                sessionStartsAt);
    }
}