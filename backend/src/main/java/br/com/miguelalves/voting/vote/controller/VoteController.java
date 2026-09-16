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

@RestController
@RequestMapping("/api/v1/voting-sessions/{votingSessionId}")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @PostMapping("/votes")
    public ResponseEntity<VoteResponse> vote(
            @PathVariable Long votingSessionId,
            @Valid @RequestBody CreateVoteRequest request) {
        var response = voteService.vote(votingSessionId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/result")
    public ResponseEntity<VotingResultResponse> getResult(
            @PathVariable Long votingSessionId) {
        return ResponseEntity.ok(voteService.getResult(votingSessionId));
    }
}
