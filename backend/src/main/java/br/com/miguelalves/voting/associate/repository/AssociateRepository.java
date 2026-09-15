package br.com.miguelalves.voting.associate.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.miguelalves.voting.associate.domain.Associate;

public interface AssociateRepository
        extends JpaRepository<Associate, Long> {

    boolean existsByCpf(String cpf);
}