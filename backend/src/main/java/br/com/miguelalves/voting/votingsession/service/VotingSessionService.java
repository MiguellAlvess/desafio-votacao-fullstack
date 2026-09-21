package br.com.miguelalves.voting.votingsession.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.voting.core.exceptions.ProposalNotFoundException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionAlreadyExistsException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionNotFoundException;
import br.com.miguelalves.voting.proposal.repository.ProposalRepository;
import br.com.miguelalves.voting.votingsession.domain.VotingSession;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionRequest;
import br.com.miguelalves.voting.votingsession.dto.OpenVotingSessionResponse;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionDetailsResponse;
import br.com.miguelalves.voting.votingsession.dto.VotingSessionResponse;
import br.com.miguelalves.voting.votingsession.mapper.VotingSessionMapper;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@Service
public class VotingSessionService {

    private static final Logger log = LoggerFactory.getLogger(VotingSessionService.class);

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
            log.warn("Voting session opening rejected: session already exists. proposalId={}", proposalId);
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
        log.info("Voting session opened. sessionId={}, proposalId={}, endsAt={}",
                savedVotingSession.id(), proposalId, savedVotingSession.endsAt());
        return votingSessionMapper.toResponse(savedVotingSession);
    }

    @Transactional(readOnly = true)
    public List<OpenVotingSessionResponse> findOpenSessions() {
        var now = LocalDateTime.now();
        return votingSessionRepository
                .findAllByStartsAtLessThanEqualAndEndsAtAfter(now, now)
                .stream()
                .map(votingSessionMapper::toOpenResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VotingSessionDetailsResponse findById(Long id) {
        var votingSession = votingSessionRepository.findDetailsById(id)
                .orElseThrow(() -> new VotingSessionNotFoundException(id));
        return votingSessionMapper.toDetailsResponse(
                votingSession,
                LocalDateTime.now());
    }
}
