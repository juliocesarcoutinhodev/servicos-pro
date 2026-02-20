CREATE TABLE tb_service_categories (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(120) NOT NULL,
    icon VARCHAR(120),
    color VARCHAR(20),
    description VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE tb_service_categories
    ADD CONSTRAINT uk_tb_service_categories_slug UNIQUE (slug);

CREATE TABLE tb_provider_services (
    id UUID PRIMARY KEY,
    provider_id UUID NOT NULL,
    category_id UUID NOT NULL,
    name VARCHAR(120) NOT NULL,
    description VARCHAR(1000),
    price_cents BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

ALTER TABLE tb_provider_services
    ADD CONSTRAINT fk_tb_provider_services_provider_id
        FOREIGN KEY (provider_id) REFERENCES tb_users (id);

ALTER TABLE tb_provider_services
    ADD CONSTRAINT fk_tb_provider_services_category_id
        FOREIGN KEY (category_id) REFERENCES tb_service_categories (id);

CREATE INDEX idx_tb_provider_services_provider_id ON tb_provider_services (provider_id);
CREATE INDEX idx_tb_provider_services_category_id ON tb_provider_services (category_id);
CREATE INDEX idx_tb_provider_services_provider_id_active ON tb_provider_services (provider_id, active);

INSERT INTO tb_service_categories (id, name, slug, icon, color, description, active, created_at, updated_at)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'Eletricista', 'eletricista', 'bolt', '#FACC15', 'Instalacao e manutencao eletrica.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000002', 'Encanador', 'encanador', 'droplet', '#3B82F6', 'Consertos e instalacoes hidraulicas.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000003', 'Diarista', 'diarista', 'sparkles', '#22C55E', 'Limpeza residencial e comercial.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000004', 'Pintor', 'pintor', 'paint-roller', '#1E40AF', 'Pintura interna e externa.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000005', 'Montador de Moveis', 'montador-moveis', 'package', '#64748B', 'Montagem e desmontagem de moveis.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000006', 'Jardineiro', 'jardineiro', 'leaf', '#16A34A', 'Manutencao de jardins e areas verdes.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000007', 'Tecnico de Ar-Condicionado', 'tecnico-ar-condicionado', 'wind', '#0EA5E9', 'Instalacao e manutencao de ar-condicionado.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000008', 'Marido de Aluguel', 'marido-de-aluguel', 'wrench', '#475569', 'Pequenos reparos gerais.', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
