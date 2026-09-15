package br.com.miguelalves.voting.associate.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateAssociateRequest(
        @NotBlank String cpf) {
}