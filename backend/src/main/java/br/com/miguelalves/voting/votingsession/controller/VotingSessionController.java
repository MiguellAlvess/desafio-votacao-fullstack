package br.com.miguelalves.voting.votingsession.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionRequest;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionResponse;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;
import br.com.miguelalves.voting.votingsession.service.VotingSessionService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.miguelalves.voting.core.api.response.ApiErrorResponse;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Sessões de votação")
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    public VotingSessionController(VotingSessionService votingSessionService) {
        this.votingSessionService = votingSessionService;
    }

    @PostMapping("/proposals/{proposalId}/sessions")
    @Operation(summary = "Abrir sessão de votação para uma pauta")
    @ApiResponse(responseCode = "201", description = "Sessão aberta")
    @ApiResponse(responseCode = "404", description = "Pauta não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Pauta já possui sessão",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<VotingSessionResponse> open(
            @PathVariable Long proposalId,
            @Valid @RequestBody OpenVotingSessionRequest request) {
        var response = votingSessionService.open(proposalId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/voting-sessions/open")
    @Operation(summary = "Listar sessões abertas")
    @ApiResponse(responseCode = "200", description = "Sessões abertas")
    public ResponseEntity<List<OpenVotingSessionResponse>> findOpenSessions() {
        return ResponseEntity.ok(votingSessionService.findOpenSessions());
    }
}
