package br.com.miguelalves.voting.associate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AssociateService.class);

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
            log.warn("Associate creation rejected: CPF already registered");
            throw new CpfAlreadyExistsException(request.cpf());
        }
        var associate = new Associate(request.cpf());
        var savedAssociate = associateRepository.save(associate);
        log.info("Associate created. associateId={}", savedAssociate.id());
        return associateMapper.toResponse(savedAssociate);
    }

    @Transactional
    public AssociateResponse identify(CreateAssociateRequest request) {
        var cpf = request.cpf().trim();
        var associate = associateRepository.findByCpf(cpf)
                .orElseGet(() -> associateRepository.save(new Associate(cpf)));
        log.info("Associate identified. associateId={}", associate.id());
        return associateMapper.toResponse(associate);
    }
}
