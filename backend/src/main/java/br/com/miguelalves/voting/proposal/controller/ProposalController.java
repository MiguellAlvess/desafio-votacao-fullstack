package br.com.miguelalves.voting.proposal.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.voting.proposal.dto.CreateProposalRequest;
import br.com.miguelalves.voting.proposal.dto.ProposalResponse;
import br.com.miguelalves.voting.proposal.service.ProposalService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.miguelalves.voting.core.api.response.ApiErrorResponse;

@RestController
@RequestMapping("/api/v1/proposals")
@Tag(name = "Pautas")
public class ProposalController {

    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar nova pauta")
    @ApiResponse(responseCode = "201", description = "Pauta cadastrada")
    @ApiResponse(responseCode = "400", description = "Título inválido",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<ProposalResponse> create(
            @Valid @RequestBody CreateProposalRequest request) {
        var response = proposalService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    @Operation(summary = "Listar pautas")
    @ApiResponse(responseCode = "200", description = "Pautas cadastradas")
    public ResponseEntity<List<ProposalResponse>> findAll() {
        return ResponseEntity.ok(proposalService.findAll());
    }
}
