package br.com.miguelalves.voting.external.eligibility;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;

import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.core.exceptions.InvalidCpfException;

@Component
public class FakeAssociateEligibilityClient implements AssociateEligibilityClient {

    private final CpfValidator cpfValidator;
    private final BooleanSupplier eligibilityResult;

    public FakeAssociateEligibilityClient() {
        this(
                new CpfValidator(),
                () -> ThreadLocalRandom.current().nextBoolean());
    }

    FakeAssociateEligibilityClient(
            CpfValidator cpfValidator,
            BooleanSupplier eligibilityResult) {
        this.cpfValidator = cpfValidator;
        this.eligibilityResult = eligibilityResult;
    }

    @Override
    public VotingEligibility check(String cpf) {
        if (!cpfValidator.isValid(cpf)) {
            throw new InvalidCpfException(cpf);
        }
        return eligibilityResult.getAsBoolean()
                ? VotingEligibility.ABLE_TO_VOTE
                : VotingEligibility.UNABLE_TO_VOTE;
    }
}
