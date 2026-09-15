package br.com.miguelalves.voting.external.eligibility;

public interface AssociateEligibilityClient {

    VotingEligibility check(String cpf);
}
