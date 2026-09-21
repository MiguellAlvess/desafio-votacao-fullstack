package br.com.miguelalves.voting.proposal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.miguelalves.voting.proposal.domain.Proposal;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    @Query("""
            SELECT new br.com.miguelalves.voting.proposal.repository.ProposalWithSession(p, vs)
            FROM Proposal p
            LEFT JOIN VotingSession vs ON vs.proposal = p
            ORDER BY p.id
            """)
    List<ProposalWithSession> findAllWithSession();
}
