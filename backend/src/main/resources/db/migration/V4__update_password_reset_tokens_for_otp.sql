ALTER TABLE tb_password_reset_tokens
    DROP CONSTRAINT IF EXISTS uk_tb_password_reset_tokens_token_hash;

DROP INDEX IF EXISTS idx_tb_password_reset_tokens_user_id_token_hash;

CREATE INDEX idx_tb_password_reset_tokens_user_id_token_hash
    ON tb_password_reset_tokens (user_id, token_hash);
