-- Adicionar coluna 'enabled' para deleção lógica em product, sale e category

ALTER TABLE product ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE sale ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE category ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT TRUE;
