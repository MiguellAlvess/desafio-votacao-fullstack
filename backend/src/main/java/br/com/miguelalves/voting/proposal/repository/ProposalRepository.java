package br.com.miguelalves.voting.proposal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.miguelalves.voting.proposal.domain.Proposal;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
}
