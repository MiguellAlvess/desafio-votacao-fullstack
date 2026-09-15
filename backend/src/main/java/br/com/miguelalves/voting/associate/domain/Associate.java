package br.com.miguelalves.voting.associate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "associate")
public class Associate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    protected Associate() {
    }

    public Associate(String cpf) {
        validateCpf(cpf);
        this.cpf = cpf.trim();
    }

    private void validateCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException(
                    "Associate CPF cannot be blank");
        }
    }

    public Long id() {
        return id;
    }

    public String cpf() {
        return cpf;
    }
}
