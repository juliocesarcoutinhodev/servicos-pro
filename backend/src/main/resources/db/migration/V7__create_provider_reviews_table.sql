CREATE TABLE tb_provider_reviews (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL,
    client_id UUID NOT NULL,
    rating SMALLINT NOT NULL,
    comment VARCHAR(1000),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE tb_provider_reviews
    ADD CONSTRAINT fk_tb_provider_reviews_provider_id
        FOREIGN KEY (provider_id) REFERENCES tb_users (id);

ALTER TABLE tb_provider_reviews
    ADD CONSTRAINT fk_tb_provider_reviews_client_id
        FOREIGN KEY (client_id) REFERENCES tb_users (id);

ALTER TABLE tb_provider_reviews
    ADD CONSTRAINT ck_tb_provider_reviews_rating
        CHECK (rating >= 1 AND rating <= 5);

CREATE INDEX idx_tb_provider_reviews_provider_id ON tb_provider_reviews (provider_id);
CREATE INDEX idx_tb_provider_reviews_client_id ON tb_provider_reviews (client_id);
CREATE INDEX idx_tb_provider_reviews_provider_id_created_at ON tb_provider_reviews (provider_id, created_at);
