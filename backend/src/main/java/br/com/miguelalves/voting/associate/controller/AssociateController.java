package br.com.miguelalves.voting.associate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.voting.associate.dto.AssociateResponse;
import br.com.miguelalves.voting.associate.dto.CreateAssociateRequest;
import br.com.miguelalves.voting.associate.service.AssociateService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import br.com.miguelalves.voting.core.api.response.ApiErrorResponse;

@RestController
@RequestMapping("/api/v1/associates")
@Tag(name = "Associados")
public class AssociateController {

    private final AssociateService associateService;

    public AssociateController(AssociateService associateService) {
        this.associateService = associateService;
    }

    @PostMapping
    @Operation(summary = "Cadastrar associado")
    @ApiResponse(responseCode = "201", description = "Associado cadastrado")
    @ApiResponse(responseCode = "400", description = "CPF não informado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "CPF já cadastrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<AssociateResponse> create(
            @Valid @RequestBody CreateAssociateRequest request) {
        var response = associateService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/identify")
    @Operation(summary = "Identificar ou cadastrar associado pelo CPF")
    @ApiResponse(responseCode = "200", description = "Associado identificado")
    @ApiResponse(responseCode = "400", description = "CPF não informado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<AssociateResponse> identify(
            @Valid @RequestBody CreateAssociateRequest request) {
        return ResponseEntity.ok(associateService.identify(request));
    }
}
