package br.com.miguelalves.voting.votingsession.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.miguelalves.voting.votingsession.domain.VotingSession;

public interface VotingSessionRepository extends JpaRepository<VotingSession, Long> {

    boolean existsByProposalId(Long proposalId);

    List<VotingSession> findAllByStartsAtLessThanEqualAndEndsAtAfter(
            LocalDateTime startsAt,
            LocalDateTime endsAt);

    @Query("""
            SELECT vs
            FROM VotingSession vs
            JOIN FETCH vs.proposal
            WHERE vs.id = :id
            """)
    Optional<VotingSession> findDetailsById(@Param("id") Long id);
}
