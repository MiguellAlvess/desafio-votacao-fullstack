package br.com.miguelalves.voting.vote.mapper;

import org.springframework.stereotype.Component;

import br.com.miguelalves.voting.vote.domain.Vote;
import br.com.miguelalves.voting.vote.dto.VoteResponse;

@Component
public class VoteMapper {

    public VoteResponse toResponse(Vote vote) {
        return new VoteResponse(
                vote.id(),
                vote.votingSession().id(),
                vote.associate().id(),
                vote.choice(),
                vote.createdAt());
    }
}
