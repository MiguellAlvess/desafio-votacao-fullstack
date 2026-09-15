package br.com.miguelalves.voting.votingsession.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionRequest;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;
import br.com.miguelalves.voting.votingsession.service.VotingSessionService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proposals/{proposalId}/sessions")
public class VotingSessionController {

    private final VotingSessionService votingSessionService;

    public VotingSessionController(VotingSessionService votingSessionService) {
        this.votingSessionService = votingSessionService;
    }

    @PostMapping
    public ResponseEntity<VotingSessionResponse> open(
            @PathVariable Long proposalId,
            @Valid @RequestBody OpenVotingSessionRequest request) {
        var response = votingSessionService.open(proposalId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
