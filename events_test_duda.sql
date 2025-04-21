-- Criação da tabela eventos
CREATE TABLE IF NOT EXISTS eventos (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    descricao TEXT NOT NULL,
    data TEXT NOT NULL,
    local TEXT NOT NULL,
    capacidade INTEGER NOT NULL
);

-- Criação da tabela palestrantes
CREATE TABLE IF NOT EXISTS palestrantes (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    curriculo TEXT NOT NULL,
    area_atuacao TEXT NOT NULL
);

-- Criação da tabela participantes
CREATE TABLE IF NOT EXISTS participantes (
    id SERIAL PRIMARY KEY,
    nome TEXT NOT NULL,
    email TEXT NOT NULL
);

-- Criação da tabela associativa evento_palestrante
CREATE TABLE IF NOT EXISTS evento_palestrante (
    evento_id INTEGER,
    palestrante_id INTEGER,
    PRIMARY KEY (evento_id, palestrante_id),
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    FOREIGN KEY (palestrante_id) REFERENCES palestrantes(id) ON DELETE CASCADE
);

-- Criação da tabela associativa evento_participante
CREATE TABLE IF NOT EXISTS evento_participante (
    evento_id INTEGER,
    participante_id INTEGER,
    PRIMARY KEY (evento_id, participante_id),
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    FOREIGN KEY (participante_id) REFERENCES participantes(id) ON DELETE CASCADE
);