package br.com.miguelalves.voting.associate.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import java.util.Optional;
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

    @Test
    void shouldReturnExistingAssociateWhenCpfIsAlreadyRegistered() {
        var request = new CreateAssociateRequest(" 12345678909 ");
        var associate = new Associate("12345678909");
        var response = new AssociateResponse(1L, "12345678909");
        when(associateRepository.findByCpf("12345678909"))
                .thenReturn(Optional.of(associate));
        when(associateMapper.toResponse(associate)).thenReturn(response);

        var result = associateService.identify(request);

        assertEquals(response, result);
        verify(associateRepository, never()).save(any());
        verify(associateMapper).toResponse(associate);
    }

    @Test
    void shouldCreateAssociateWhenCpfIsNotRegistered() {
        var request = new CreateAssociateRequest("12345678909");
        var savedAssociate = new Associate("12345678909");
        var response = new AssociateResponse(1L, "12345678909");
        when(associateRepository.findByCpf("12345678909"))
                .thenReturn(Optional.empty());
        when(associateRepository.save(any(Associate.class)))
                .thenReturn(savedAssociate);
        when(associateMapper.toResponse(savedAssociate)).thenReturn(response);

        var result = associateService.identify(request);

        assertEquals(response, result);
        verify(associateRepository).save(any(Associate.class));
        verify(associateMapper).toResponse(savedAssociate);
    }
}
