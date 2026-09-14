CREATE TABLE proposal (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE voting_session (
    id BIGSERIAL PRIMARY KEY,
    proposal_id BIGINT NOT NULL UNIQUE,
    starts_at TIMESTAMP NOT NULL,
    ends_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_voting_session_proposal
        FOREIGN KEY (proposal_id)
        REFERENCES proposal(id)
);

CREATE TABLE vote (
    id BIGSERIAL PRIMARY KEY,
    voting_session_id BIGINT NOT NULL,
    associate_id VARCHAR(100) NOT NULL,
    choice VARCHAR(3) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_vote_voting_session
        FOREIGN KEY (voting_session_id)
        REFERENCES voting_session(id),

    CONSTRAINT uk_vote_session_associate
        UNIQUE (voting_session_id, associate_id),

    CONSTRAINT chk_vote_choice
        CHECK (choice IN ('YES', 'NO'))
);