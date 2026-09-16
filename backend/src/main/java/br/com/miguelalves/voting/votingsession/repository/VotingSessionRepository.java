package br.com.miguelalves.voting.votingsession.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.miguelalves.voting.votingsession.domain.VotingSession;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    boolean existsByProposalId(Long proposalId);

    List<VotingSession> findAllByStartsAtLessThanEqualAndEndsAtAfter(
            LocalDateTime startsAt,
            LocalDateTime endsAt);
}
