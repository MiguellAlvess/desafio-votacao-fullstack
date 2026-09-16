package br.com.miguelalves.voting.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.miguelalves.voting.vote.domain.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVotingSession_IdAndAssociate_Id(
            Long votingSessionId,
            Long associateId);

    @Query("""
            SELECT
                COALESCE(SUM(CASE WHEN v.choice = br.com.miguelalves.voting.vote.domain.VoteChoice.YES THEN 1 ELSE 0 END), 0) AS yesVotes,
                COALESCE(SUM(CASE WHEN v.choice = br.com.miguelalves.voting.vote.domain.VoteChoice.NO THEN 1 ELSE 0 END), 0) AS noVotes
            FROM Vote v
            WHERE v.votingSession.id = :votingSessionId
            """)
    VoteCount countByVotingSessionId(@Param("votingSessionId") Long votingSessionId);
}
