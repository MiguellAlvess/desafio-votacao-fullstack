package br.com.miguelalves.voting.associate.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.voting.associate.domain.Associate;
import br.com.miguelalves.voting.associate.dto.AssociateResponse;
import br.com.miguelalves.voting.associate.dto.CreateAssociateRequest;
import br.com.miguelalves.voting.associate.mapper.AssociateMapper;
import br.com.miguelalves.voting.associate.repository.AssociateRepository;
import br.com.miguelalves.voting.core.exceptions.CpfAlreadyExistsException;

@Service
public class AssociateService {

    private final AssociateRepository associateRepository;
    private final AssociateMapper associateMapper;

    public AssociateService(
            AssociateRepository associateRepository,
            AssociateMapper associateMapper) {
        this.associateRepository = associateRepository;
        this.associateMapper = associateMapper;
    }

    @Transactional
    public AssociateResponse create(CreateAssociateRequest request) {
        if (associateRepository.existsByCpf(request.cpf())) {
            throw new CpfAlreadyExistsException(request.cpf());
        }
        var associate = new Associate(request.cpf());
        var savedAssociate = associateRepository.save(associate);
        return associateMapper.toResponse(savedAssociate);
    }
}