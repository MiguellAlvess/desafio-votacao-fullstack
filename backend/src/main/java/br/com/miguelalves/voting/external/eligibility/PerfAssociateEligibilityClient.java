package br.com.miguelalves.voting.external.eligibility;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.core.exceptions.InvalidCpfException;

@Component
@Profile("perf")
public class PerfAssociateEligibilityClient implements AssociateEligibilityClient {

    private final CpfValidator cpfValidator = new CpfValidator();

    @Override
    public VotingEligibility check(String cpf) {
        if (!cpfValidator.isValid(cpf)) {
            throw new InvalidCpfException(cpf);
        }
        return VotingEligibility.ABLE_TO_VOTE;
    }
}
