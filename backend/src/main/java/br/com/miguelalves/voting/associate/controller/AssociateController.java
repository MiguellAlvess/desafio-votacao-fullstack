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

@RestController
@RequestMapping("/api/v1/associates")
public class AssociateController {

    private final AssociateService associateService;

    public AssociateController(AssociateService associateService) {
        this.associateService = associateService;
    }

    @PostMapping
    public ResponseEntity<AssociateResponse> create(
            @Valid @RequestBody CreateAssociateRequest request) {
        var response = associateService.create(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/identify")
    public ResponseEntity<AssociateResponse> identify(
            @Valid @RequestBody CreateAssociateRequest request) {
        return ResponseEntity.ok(associateService.identify(request));
    }
}
