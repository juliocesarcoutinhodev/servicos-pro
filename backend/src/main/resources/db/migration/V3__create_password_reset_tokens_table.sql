CREATE TABLE tb_password_reset_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE tb_password_reset_tokens
    ADD CONSTRAINT fk_tb_password_reset_tokens_user_id
        FOREIGN KEY (user_id) REFERENCES tb_users (id);

ALTER TABLE tb_password_reset_tokens
    ADD CONSTRAINT uk_tb_password_reset_tokens_token_hash UNIQUE (token_hash);

CREATE INDEX idx_tb_password_reset_tokens_user_id ON tb_password_reset_tokens (user_id);
CREATE INDEX idx_tb_password_reset_tokens_expires_at ON tb_password_reset_tokens (expires_at);
CREATE INDEX idx_tb_password_reset_tokens_used_at ON tb_password_reset_tokens (used_at);
