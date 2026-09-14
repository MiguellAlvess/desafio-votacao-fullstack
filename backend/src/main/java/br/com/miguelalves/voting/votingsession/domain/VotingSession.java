package br.com.miguelalves.voting.votingsession.domain;

import java.time.Duration;
import java.time.LocalDateTime;

import br.com.miguelalves.voting.proposal.domain.Proposal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "voting_session")
public class VotingSession {

    private static final Duration DEFAULT_DURATION = Duration.ofMinutes(1);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "proposal_id", nullable = false, unique = true)
    private Proposal proposal;

    @Column(name = "starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private LocalDateTime endsAt;

    protected VotingSession() {
    }

    public VotingSession(
            Proposal proposal,
            Duration duration,
            LocalDateTime startsAt) {
        validateProposal(proposal);
        validateStartsAt(startsAt);
        var effectiveDuration = duration == null
                ? DEFAULT_DURATION
                : duration;
        validateDuration(effectiveDuration);
        this.proposal = proposal;
        this.startsAt = startsAt;
        this.endsAt = startsAt.plus(effectiveDuration);
    }

    private void validateProposal(Proposal proposal) {
        if (proposal == null) {
            throw new IllegalArgumentException("Proposal cannot be null");
        }
    }

    private void validateStartsAt(LocalDateTime startsAt) {
        if (startsAt == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
    }

    private void validateDuration(Duration duration) {
        if (duration.isZero() || duration.isNegative()) {
            throw new IllegalArgumentException(
                    "Voting session duration must be greater than zero");
        }
    }

    public boolean isOpenAt(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("Date time cannot be null");
        }
        return !dateTime.isBefore(startsAt)
                && dateTime.isBefore(endsAt);
    }

    public Long id() {
        return id;
    }

    public Proposal proposal() {
        return proposal;
    }

    public LocalDateTime startsAt() {
        return startsAt;
    }

    public LocalDateTime endsAt() {
        return endsAt;
    }
}
