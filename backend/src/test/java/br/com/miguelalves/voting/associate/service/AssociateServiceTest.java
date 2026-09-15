package br.com.miguelalves.voting.associate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.miguelalves.voting.associate.domain.Associate;
import br.com.miguelalves.voting.associate.dto.AssociateResponse;
import br.com.miguelalves.voting.associate.dto.CreateAssociateRequest;
import br.com.miguelalves.voting.associate.mapper.AssociateMapper;
import br.com.miguelalves.voting.associate.repository.AssociateRepository;
import br.com.miguelalves.voting.core.exceptions.CpfAlreadyExistsException;

@ExtendWith(MockitoExtension.class)
class AssociateServiceTest {

    @Mock
    private AssociateRepository associateRepository;

    @Mock
    private AssociateMapper associateMapper;

    private AssociateService associateService;

    @BeforeEach
    void setUp() {
        associateService = new AssociateService(
                associateRepository,
                associateMapper);
    }

    @Test
    void shouldCreateAssociateWhenCpfDoesNotExist() {
        var request = new CreateAssociateRequest("12345678901");
        var associate = new Associate("12345678901");
        var response = new AssociateResponse(
                1L,
                "12345678901");

        when(associateRepository.existsByCpf(
                request.cpf()))
                .thenReturn(false);
        when(associateRepository.save(any(Associate.class)))
                .thenReturn(associate);

        when(associateMapper.toResponse(associate))
                .thenReturn(response);
        var result = associateService.create(request);

        assertEquals(1L, result.id());
        assertEquals(
                "12345678901",
                result.cpf());
    }

    @Test
    void shouldThrowExceptionWhenCpfAlreadyExists() {
        var request = new CreateAssociateRequest("12345678901");
        when(associateRepository.existsByCpf(
                request.cpf()))
                .thenReturn(true);
        var exception = assertThrows(
                CpfAlreadyExistsException.class,
                () -> associateService.create(request));

        assertEquals(
                "An associate with CPF 12345678901 already exists",
                exception.getMessage());
    }
}