package br.com.miguelalves.voting.votingsession.service;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.voting.core.exceptions.ProposalNotFoundException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionAlreadyExistsException;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionRequest;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;
import br.com.miguelalves.voting.votingsession.mapper.VotingSessionMapper;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@Service
public class VotingSessionService {

    private final VotingSessionRepository votingSessionRepository;
    private final ProposalRepository proposalRepository;
    private final VotingSessionMapper votingSessionMapper;

    public VotingSessionService(
            VotingSessionRepository votingSessionRepository,
            ProposalRepository proposalRepository,
            VotingSessionMapper votingSessionMapper) {
        this.votingSessionRepository = votingSessionRepository;
        this.proposalRepository = proposalRepository;
        this.votingSessionMapper = votingSessionMapper;
    }

    @Transactional
    public VotingSessionResponse open(
            Long proposalId,
            OpenVotingSessionRequest request) {
        var proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new ProposalNotFoundException(proposalId));

        if (votingSessionRepository.existsByProposalId(proposalId)) {
            throw new VotingSessionAlreadyExistsException(proposalId);
        }

        var duration = request.durationInMinutes() == null
                ? null
                : Duration.ofMinutes(request.durationInMinutes());
        var votingSession = new VotingSession(
                proposal,
                duration,
                LocalDateTime.now());
        var savedVotingSession = votingSessionRepository.save(votingSession);
        return votingSessionMapper.toResponse(savedVotingSession);
    }
}
