CREATE TABLE associate (
    id BIGSERIAL PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE
);

ALTER TABLE vote
    DROP CONSTRAINT uk_vote_session_associate;

ALTER TABLE vote
    DROP COLUMN associate_id;

ALTER TABLE vote
    ADD COLUMN associate_id BIGINT NOT NULL;

ALTER TABLE vote
    ADD CONSTRAINT fk_vote_associate
        FOREIGN KEY (associate_id)
        REFERENCES associate(id);

ALTER TABLE vote
    ADD CONSTRAINT uk_vote_session_associate
        UNIQUE (voting_session_id, associate_id);