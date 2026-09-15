package br.com.miguelalves.voting.vote.domain;

import java.time.LocalDateTime;

import br.com.miguelalves.voting.associate.domain.Associate;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "vote", uniqueConstraints = {
        @UniqueConstraint(name = "uk_vote_session_associate", columnNames = {
                "voting_session_id",
                "associate_id"
        })
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "voting_session_id", nullable = false)
    private VotingSession votingSession;

    @ManyToOne(optional = false)
    @JoinColumn(name = "associate_id", nullable = false)
    private Associate associate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private VoteChoice choice;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Vote() {
    }

    public Vote(
            VotingSession votingSession,
            Associate associate,
            VoteChoice choice,
            LocalDateTime createdAt) {
        validateVotingSession(votingSession);
        validateAssociate(associate);
        validateChoice(choice);
        validateCreatedAt(createdAt);
        this.votingSession = votingSession;
        this.associate = associate;
        this.choice = choice;
        this.createdAt = createdAt;
    }

    private void validateVotingSession(
            VotingSession votingSession) {

        if (votingSession == null) {
            throw new IllegalArgumentException(
                    "Voting session cannot be null");
        }
    }

    private void validateAssociate(Associate associate) {
        if (associate == null) {
            throw new IllegalArgumentException(
                    "Associate cannot be null");
        }
    }

    private void validateChoice(VoteChoice choice) {
        if (choice == null) {
            throw new IllegalArgumentException(
                    "Vote choice cannot be null");
        }
    }

    private void validateCreatedAt(
            LocalDateTime createdAt) {
        if (createdAt == null) {
            throw new IllegalArgumentException(
                    "Creation time cannot be null");
        }
    }

    public Long id() {
        return id;
    }

    public VotingSession votingSession() {
        return votingSession;
    }

    public Associate associate() {
        return associate;
    }

    public VoteChoice choice() {
        return choice;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }
}
