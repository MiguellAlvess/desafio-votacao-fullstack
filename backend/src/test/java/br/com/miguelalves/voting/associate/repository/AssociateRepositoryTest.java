package br.com.miguelalves.voting.associate.repository;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import br.com.miguelalves.voting.associate.domain.Associate;

@Testcontainers
@DataJpaTest
class AssociateRepositoryTest {

        @Container
        @ServiceConnection
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

        @Autowired
        private AssociateRepository associateRepository;

        @Test
        void shouldSaveAssociate() {
                var associate = new Associate("12345678901");

                var savedAssociate = associateRepository.save(associate);

                assertThat(savedAssociate.id())
                                .isNotNull();
                assertThat(savedAssociate.cpf())
                                .isEqualTo("12345678901");
        }

        @Test
        void shouldFindAssociateById() {
                var associate = associateRepository.save(
                                new Associate("12345678901"));

                var foundAssociate = associateRepository.findById(
                                associate.id());

                assertThat(foundAssociate)
                                .isPresent();
                assertThat(foundAssociate.get().cpf())
                                .isEqualTo("12345678901");
        }

        @Test
        void shouldReturnTrueWhenCpfExists() {
                associateRepository.save(
                                new Associate("12345678901"));

                var exists = associateRepository.existsByCpf(
                                "12345678901");
                assertThat(exists).isTrue();
        }

        @Test
        void shouldReturnFalseWhenCpfDoesNotExist() {
                var exists = associateRepository.existsByCpf(
                                "12345678901");

                assertThat(exists).isFalse();
        }

        @Test
        void shouldFindAssociateByCpf() {
                var associate = associateRepository.save(new Associate("12345678909"));

                var foundAssociate = associateRepository.findByCpf("12345678909");

                assertThat(foundAssociate).isPresent();
                assertThat(foundAssociate.get().id()).isEqualTo(associate.id());
        }

        @Test
        void shouldReturnEmptyWhenCpfDoesNotExist() {
                assertThat(associateRepository.findByCpf("12345678909")).isEmpty();
        }
}
