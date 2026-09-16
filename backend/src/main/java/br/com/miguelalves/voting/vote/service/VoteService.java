package br.com.miguelalves.voting.vote.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.miguelalves.voting.associate.repository.AssociateRepository;
import br.com.miguelalves.voting.core.exceptions.AssociateAlreadyVotedException;
import br.com.miguelalves.voting.core.exceptions.AssociateNotFoundException;
import br.com.miguelalves.voting.core.exceptions.AssociateUnableToVoteException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionClosedException;
import br.com.miguelalves.voting.core.exceptions.VotingSessionNotFoundException;
import br.com.miguelalves.voting.external.eligibility.AssociateEligibilityClient;
import br.com.miguelalves.voting.external.eligibility.VotingEligibility;
import br.com.miguelalves.voting.vote.domain.Vote;
import br.com.miguelalves.voting.vote.dto.CreateVoteRequest;
import br.com.miguelalves.voting.vote.dto.VoteResponse;
import br.com.miguelalves.voting.vote.dto.VotingResultResponse;
import br.com.miguelalves.voting.vote.mapper.VoteMapper;
import br.com.miguelalves.voting.vote.repository.VoteRepository;
import br.com.miguelalves.voting.votingsession.repository.VotingSessionRepository;

@Service
public class VoteService {

    private static final Logger log = LoggerFactory.getLogger(VoteService.class);

    private final VotingSessionRepository votingSessionRepository;
    private final AssociateRepository associateRepository;
    private final VoteRepository voteRepository;
    private final AssociateEligibilityClient associateEligibilityClient;
    private final VoteMapper voteMapper;

    public VoteService(
            VotingSessionRepository votingSessionRepository,
            AssociateRepository associateRepository,
            VoteRepository voteRepository,
            AssociateEligibilityClient associateEligibilityClient,
            VoteMapper voteMapper) {
        this.votingSessionRepository = votingSessionRepository;
        this.associateRepository = associateRepository;
        this.voteRepository = voteRepository;
        this.associateEligibilityClient = associateEligibilityClient;
        this.voteMapper = voteMapper;
    }

    @Transactional
    public VoteResponse vote(
            Long votingSessionId,
            CreateVoteRequest request) {
        var votingSession = votingSessionRepository.findById(votingSessionId)
                .orElseThrow(() -> new VotingSessionNotFoundException(votingSessionId));
        var now = LocalDateTime.now();
        if (!votingSession.isOpenAt(now)) {
            log.warn("Vote attempt on closed session. sessionId={}, associateId={}",
                    votingSessionId, request.associateId());
            throw new VotingSessionClosedException(votingSessionId);
        }
        var associate = associateRepository.findById(request.associateId())
                .orElseThrow(() -> new AssociateNotFoundException(request.associateId()));
        if (voteRepository.existsByVotingSession_IdAndAssociate_Id(
                votingSessionId,
                request.associateId())) {
            log.warn("Duplicate vote attempt. sessionId={}, associateId={}",
                    votingSessionId, request.associateId());
            throw new AssociateAlreadyVotedException(
                    request.associateId(),
                    votingSessionId);
        }
        var eligibility = associateEligibilityClient.check(associate.cpf());
        if (eligibility == VotingEligibility.UNABLE_TO_VOTE) {
            log.warn("Associate unable to vote. sessionId={}, associateId={}",
                    votingSessionId, request.associateId());
            throw new AssociateUnableToVoteException(associate.cpf());
        }
        var vote = new Vote(
                votingSession,
                associate,
                request.choice(),
                now);
        var savedVote = voteRepository.save(vote);
        log.info("Vote registered. sessionId={}, associateId={}, choice={}",
                votingSessionId, request.associateId(), savedVote.choice());
        return voteMapper.toResponse(savedVote);
    }

    @Transactional(readOnly = true)
    public VotingResultResponse getResult(Long votingSessionId) {
        votingSessionRepository.findById(votingSessionId)
                .orElseThrow(() -> new VotingSessionNotFoundException(votingSessionId));

        var count = voteRepository.countByVotingSessionId(votingSessionId);
        var yesVotes = count.getYesVotes();
        var noVotes = count.getNoVotes();
        return new VotingResultResponse(
                votingSessionId,
                yesVotes,
                noVotes,
                yesVotes + noVotes);
    }
}
