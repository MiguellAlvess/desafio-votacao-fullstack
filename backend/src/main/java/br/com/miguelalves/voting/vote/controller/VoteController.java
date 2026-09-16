package br.com.miguelalves.voting.vote.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.voting.vote.dto.CreateVoteRequest;
import br.com.miguelalves.voting.vote.dto.VoteResponse;
import br.com.miguelalves.voting.vote.dto.VotingResultResponse;
import br.com.miguelalves.voting.vote.service.VoteService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.miguelalves.voting.core.api.response.ApiErrorResponse;

@RestController
@RequestMapping("/api/v1/voting-sessions/{votingSessionId}")
@Tag(name = "Votos")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/votes")
    @Operation(summary = "Registrar voto")
    @ApiResponse(responseCode = "201", description = "Voto registrado")
    @ApiResponse(responseCode = "400", description = "Dados do voto inválidos",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Sessão, associado ou elegibilidade não encontrados",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Sessão fechada ou voto duplicado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<VoteResponse> vote(
            @PathVariable Long votingSessionId,
            @Valid @RequestBody CreateVoteRequest request) {
        var response = voteService.vote(votingSessionId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/result")
    @Operation(summary = "Consultar contabilização dos votos")
    @ApiResponse(responseCode = "200", description = "Contabilização dos votos")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<VotingResultResponse> getResult(
            @PathVariable Long votingSessionId) {
        return ResponseEntity.ok(voteService.getResult(votingSessionId));
    }
}
