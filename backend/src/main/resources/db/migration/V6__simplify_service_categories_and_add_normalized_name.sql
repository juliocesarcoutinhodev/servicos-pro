ALTER TABLE tb_service_categories
    ADD COLUMN normalized_name VARCHAR(120);

UPDATE tb_service_categories
SET normalized_name = LOWER(
        REGEXP_REPLACE(
                REGEXP_REPLACE(
                        TRIM(
                                TRANSLATE(
                                        name,
                                        'ÁÀÂÃÄáàâãäÉÈÊËéèêëÍÌÎÏíìîïÓÒÔÕÖóòôõöÚÙÛÜúùûüÇçÑñ',
                                        'AAAAAaaaaaEEEEeeeeIIIIiiiiOOOOOoooooUUUUuuuuCcNn'
                                )
                        ),
                        '[^a-zA-Z0-9\s]',
                        ' ',
                        'g'
                ),
                '\s+',
                ' ',
                'g'
        )
)
WHERE normalized_name IS NULL;

ALTER TABLE tb_service_categories
    ALTER COLUMN normalized_name SET NOT NULL;

ALTER TABLE tb_service_categories
    DROP CONSTRAINT IF EXISTS uk_tb_service_categories_slug;

ALTER TABLE tb_service_categories
    DROP COLUMN IF EXISTS slug;

ALTER TABLE tb_service_categories
    DROP COLUMN IF EXISTS icon;

ALTER TABLE tb_service_categories
    DROP COLUMN IF EXISTS color;

ALTER TABLE tb_service_categories
    ADD CONSTRAINT uk_tb_service_categories_normalized_name UNIQUE (normalized_name);
