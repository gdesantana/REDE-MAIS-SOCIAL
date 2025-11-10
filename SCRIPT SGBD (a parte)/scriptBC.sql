-- ---------------------------------------------------
-- 1. TABELAS DE IDENTIFICAÇÃO E LOGIN (Base)
-- ---------------------------------------------------

-- Tabela principal para Login e Dados Comuns
CREATE TABLE Entidade (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL
);

-- Tabela que representa o Agente (PF ou PJ) no contexto de busca/vagas.
CREATE TABLE Candidato (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES Entidade(id)
);

-- Tabela para Pessoas Físicas (Herda de Entidade)
CREATE TABLE PessoaFisica (
    id INT PRIMARY KEY,
    cpf VARCHAR(11) NOT NULL UNIQUE,
    FOREIGN KEY (id) REFERENCES Entidade(id)
);

-- Tabela para Pessoas Jurídicas (Herda de Entidade)
CREATE TABLE PessoaJuridica (
    id INT PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    razao_social VARCHAR(150) NOT NULL,
    nome_fantasia VARCHAR(100),
    cnae VARCHAR(10),
    natureza_juridica VARCHAR(10),
    FOREIGN KEY (id) REFERENCES Entidade(id)
);


-- ---------------------------------------------------
-- 2. TABELAS DE LOCALIZAÇÃO E CONTATO
-- ---------------------------------------------------

-- Tabela que agrupa dados de Localização (Seu MER possui esta tabela)
CREATE TABLE Localizacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL,
    -- Campos básicos de localização (ex: tipo principal)
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);

-- Endereço Geográfico (Relacionado à Entidade)
CREATE TABLE GEOGRAFICA (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL, -- FK para Entidade
    logradouro VARCHAR(100) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    bairro VARCHAR(100),
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    cep VARCHAR(8),
    complemento VARCHAR(100),
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);

-- Telefones (Relacionado à Entidade)
CREATE TABLE FONE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL, -- FK para Entidade
    tipo VARCHAR(50) NOT NULL, 
    numero_completo VARCHAR(20) NOT NULL,
    ddd VARCHAR(3),
    ddi VARCHAR(3),
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);

-- Contatos Virtuais (Relacionado à Entidade)
CREATE TABLE `VIRTUAL` (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL, -- FK para Entidade
    tipo VARCHAR(50) NOT NULL,
    contato VARCHAR(255) NOT NULL,
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);


-- ---------------------------------------------------
-- 3. TABELAS DE PERFIL, HABILIDADE E INTERESSE
-- ---------------------------------------------------

-- Perfil do Candidato (Ligada ao Candidato)
CREATE TABLE Perfil (
    id INT PRIMARY KEY AUTO_INCREMENT,
    candidato_id INT UNIQUE NOT NULL, 
    descricao TEXT,
    FOREIGN KEY (candidato_id) REFERENCES Candidato(id)
);

-- Catálogo de Habilidades
CREATE TABLE Habilidade (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- Catálogo de Interesses
CREATE TABLE Interesse (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- Relacionamento N:M entre Perfil e Habilidade
CREATE TABLE Perfil_Habilidade (
    perfil_id INT NOT NULL,
    habilidade_id INT NOT NULL,
    PRIMARY KEY (perfil_id, habilidade_id),
    FOREIGN KEY (perfil_id) REFERENCES Perfil(id),
    FOREIGN KEY (habilidade_id) REFERENCES Habilidade(id)
);

-- Relacionamento N:M entre Perfil e Interesse
CREATE TABLE Perfil_Interesse (
    perfil_id INT NOT NULL,
    interesse_id INT NOT NULL,
    PRIMARY KEY (perfil_id, interesse_id),
    FOREIGN KEY (perfil_id) REFERENCES Perfil(id),
    FOREIGN KEY (interesse_id) REFERENCES Interesse(id)
);

-- Tabela de Formação (Ligada ao Candidato)
CREATE TABLE Formacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    candidato_id INT NOT NULL,
    instituicao VARCHAR(150),
    curso VARCHAR(100),
    data_inicio DATE,
    data_fim DATE,
    FOREIGN KEY (candidato_id) REFERENCES Candidato(id)
);


-- ---------------------------------------------------
-- 4. TABELAS DE GOVERNANÇA E DOCUMENTAÇÃO (Termos, Links, etc.)
-- ---------------------------------------------------

-- Tabela para Aceitação do Termo de Compromisso (Passo 5)
CREATE TABLE Termo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    candidato_id INT UNIQUE NOT NULL, 
    data_aceitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (candidato_id) REFERENCES Candidato(id)
);

-- Tabela de Afiliação (Registro do processo e status)
CREATE TABLE Afilicao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    candidato_id INT NOT NULL,
    data_solicitacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) NOT NULL,
    FOREIGN KEY (candidato_id) REFERENCES Candidato(id)
);

-- Tabela de Link (Recursos externos)
CREATE TABLE Link (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL,
    url VARCHAR(255) NOT NULL,
    descricao VARCHAR(100),
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);

-- Tabela de Consentimento (LGPD/Uso de Dados)
CREATE TABLE Consentimento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL,
    tipo VARCHAR(50) NOT NULL, 
    status BOOLEAN DEFAULT TRUE,
    data_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);


-- ---------------------------------------------------
-- 5. TABELAS DE APOIO E ESTRUTURA (Conforme suas imagens)
-- ---------------------------------------------------

-- Estrutura de Templates (UI/Documentação)
CREATE TABLE Template (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) UNIQUE NOT NULL
);

-- Conteúdo textual para templates/documentos
CREATE TABLE Texto (
    id INT PRIMARY KEY AUTO_INCREMENT,
    template_id INT NOT NULL,
    conteudo TEXT NOT NULL,
    FOREIGN KEY (template_id) REFERENCES Template(id)
);

-- Elementos de UI/Formulário
CREATE TABLE Elemento (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) UNIQUE NOT NULL
);

-- Regras de Validação para Elementos de Formulário
CREATE TABLE Validacao (
    id INT PRIMARY KEY AUTO_INCREMENT,
    elemento_id INT NOT NULL,
    regra VARCHAR(255) NOT NULL,
    FOREIGN KEY (elemento_id) REFERENCES Elemento(id)
);

-- Itens específicos de um Termo
CREATE TABLE ItemTermo (
    id INT PRIMARY KEY AUTO_INCREMENT,
    termo_id INT NOT NULL,
    descricao VARCHAR(255) NOT NULL,
    FOREIGN KEY (termo_id) REFERENCES Termo(id)
);

-- Itens de Acesso (Permissões)
CREATE TABLE ItemAcesso (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL,
    permissao VARCHAR(100) NOT NULL,
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);

-- Tabela Identidade (Seu MER parece ter esta tabela para dados de identificação)
CREATE TABLE Identidade (
    id INT PRIMARY KEY AUTO_INCREMENT,
    entidade_id INT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    valor VARCHAR(100) NOT NULL,
    FOREIGN KEY (entidade_id) REFERENCES Entidade(id)
);