package br.com.miguelalves.voting.vote.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.miguelalves.voting.vote.domain.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByVotingSessionIdAndAssociateId(
            Long votingSessionId,
            String associateId);
}