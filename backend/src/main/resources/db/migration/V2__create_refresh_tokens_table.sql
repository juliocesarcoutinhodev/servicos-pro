CREATE TABLE tb_refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE tb_refresh_tokens
    ADD CONSTRAINT fk_tb_refresh_tokens_user_id
        FOREIGN KEY (user_id) REFERENCES tb_users (id);

ALTER TABLE tb_refresh_tokens
    ADD CONSTRAINT uk_tb_refresh_tokens_token_hash UNIQUE (token_hash);

CREATE INDEX idx_tb_refresh_tokens_user_id ON tb_refresh_tokens (user_id);
CREATE INDEX idx_tb_refresh_tokens_expires_at ON tb_refresh_tokens (expires_at);
