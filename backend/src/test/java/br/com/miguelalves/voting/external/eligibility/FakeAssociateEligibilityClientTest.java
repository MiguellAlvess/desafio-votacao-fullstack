package br.com.miguelalves.voting.external.eligibility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import br.com.miguelalves.voting.core.exceptions.InvalidCpfException;

class FakeAssociateEligibilityClientTest {

    private final CpfValidator cpfValidator = new CpfValidator();

    @Test
    void shouldReturnAbleToVoteForValidCpfWhenRandomResultAllowsVoting() {
        var client = new FakeAssociateEligibilityClient(
                cpfValidator,
                () -> true);

        var result = client.check("52998224725");

        assertThat(result).isEqualTo(VotingEligibility.ABLE_TO_VOTE);
    }

    @Test
    void shouldReturnUnableToVoteForValidCpfWhenRandomResultDeniesVoting() {
        var client = new FakeAssociateEligibilityClient(
                cpfValidator,
                () -> false);

        var result = client.check("52998224725");

        assertThat(result).isEqualTo(VotingEligibility.UNABLE_TO_VOTE);
    }

    @Test
    void shouldThrowInvalidCpfExceptionWhenCpfIsInvalid() {
        var client = new FakeAssociateEligibilityClient(
                cpfValidator,
                () -> true);

        assertThatThrownBy(() -> client.check("12345678901"))
                .isInstanceOf(InvalidCpfException.class)
                .hasMessage("CPF 12345678901 is invalid");
    }
}
