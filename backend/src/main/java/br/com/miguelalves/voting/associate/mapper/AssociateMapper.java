package br.com.miguelalves.voting.associate.mapper;

import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.associate.domain.Associate;
import br.com.miguelalves.voting.associate.dto.AssociateResponse;

@Component
public class AssociateMapper {

    public AssociateResponse toResponse(Associate associate) {
        return new AssociateResponse(
                associate.id(),
                associate.cpf());
    }
}
