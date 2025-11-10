package br.com.redemaissocial.dao;

import br.com.redemaissocial.domain.Candidato;
import br.com.redemaissocial.domain.Perfil;
import br.com.redemaissocial.domain.Geografica;
import br.com.redemaissocial.domain.Fone;
import br.com.redemaissocial.domain.Virtual;
import br.com.redemaissocial.infra.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CandidatoDAO {

    private static final String SENHA_PADRAO_HASH = "SENHA_PADRAO_123";

    // --- métodos auxiliares e de identificação (passo 1 e 2) ---

    // busca um candidato pelo cpf ou cnpj para checagem de duplicidade
    public Candidato buscarPorDocumento(String documento) {
        String sql = "SELECT e.id, e.nome, pf.cpf, pj.cnpj " +
                     "FROM Entidade e " +
                     "LEFT JOIN PessoaFisica pf ON e.id = pf.id " +
                     "LEFT JOIN PessoaJuridica pj ON e.id = pj.id " +
                     "WHERE pf.cpf = ? OR pj.cnpj = ?";
        
        String docLimpo = documento.replaceAll("[^0-9]", "");
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, docLimpo); // tenta buscar por cpf
            stmt.setString(2, docLimpo); // tenta buscar por cnpj
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Candidato candidato = new Candidato();
                    // verifica se o resultado é cpf ou cnpj e preenche o objeto
                    candidato.setId(rs.getInt("id"));
                    candidato.setNome(rs.getString("nome"));
                    candidato.setDocumento(rs.getString("cpf") != null ? rs.getString("cpf") : rs.getString("cnpj"));
                    return candidato; 
                }
            }
        } catch (SQLException e) {
            System.err.println("erro de persistência ao buscar candidato por documento: " + e.getMessage());
        }
        return null;
    }

    // insere o registro base na tabela 'entidade' e retorna o id gerado
    private Integer inserirEntidade(Connection conn, Candidato candidato, String senhaHash) throws SQLException {
        // usa o nome fantasia (pj) ou o nome (pf) para a entidade
        String nomeEntidade = (candidato.getNomeFantasia() != null && !candidato.getNomeFantasia().isEmpty()) ? candidato.getNomeFantasia() : candidato.getNome();
        
        String sqlEntidade = "INSERT INTO Entidade (nome, email, senha_hash) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmtEntidade = conn.prepareStatement(sqlEntidade, Statement.RETURN_GENERATED_KEYS)) {
            
            stmtEntidade.setString(1, nomeEntidade);
            stmtEntidade.setString(2, candidato.getEmail()); 
            stmtEntidade.setString(3, senhaHash); 
            stmtEntidade.executeUpdate();

            // pega o id gerado para usar nas tabelas filhas (pessoafisica/pessoajuridica)
            try (ResultSet rs = stmtEntidade.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return null;
    }

    // persiste o candidato como pessoa física
    public Candidato salvarCandidatoPF(Candidato candidato, String senhaHash) {
        String sqlPF = "INSERT INTO PessoaFisica (id, cpf) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtPF = conn.prepareStatement(sqlPF)) {
            
            // 1. insere na entidade e pega o id
            Integer idGerado = inserirEntidade(conn, candidato, senhaHash);
            
            if (idGerado != null) {
                // 2. insere na pessoafisica, usando o mesmo id da entidade
                candidato.setId(idGerado); 
                String docLimpo = candidato.getDocumento().replaceAll("[^0-9]", "");
                
                stmtPF.setInt(1, idGerado);
                stmtPF.setString(2, docLimpo); 
                stmtPF.executeUpdate();
                return candidato; 
            }
        } catch (SQLException e) {
            System.err.println("erro ao persistir novo candidato pessoa física: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // persiste o candidato como pessoa jurídica
    public Candidato salvarCandidatoPJ(Candidato candidato, String senhaHash) {
        String sqlPJ = "INSERT INTO PessoaJuridica (id, cnpj, razao_social, nome_fantasia, cnae, natureza_juridica) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtPJ = conn.prepareStatement(sqlPJ)) {
            
            // 1. insere na entidade e pega o id
            Integer idGerado = inserirEntidade(conn, candidato, senhaHash);

            if (idGerado != null) {
                // 2. insere na pessoajuridica, usando o mesmo id da entidade e os campos extras
                candidato.setId(idGerado); 
                String docLimpo = candidato.getDocumento().replaceAll("[^0-9]", "");
                
                stmtPJ.setInt(1, idGerado);
                stmtPJ.setString(2, docLimpo); 
                
                // campos específicos de pj
                stmtPJ.setString(3, candidato.getRazaoSocial());
                stmtPJ.setString(4, candidato.getNomeFantasia());
                stmtPJ.setString(5, candidato.getCnae());
                stmtPJ.setString(6, candidato.getNaturezaJuridica());
                
                stmtPJ.executeUpdate();
                return candidato; 
            }
        } catch (SQLException e) {
            System.err.println("erro ao persistir novo candidato pessoa jurídica: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // ---------------------- métodos de localização e contato (passo 3) ----------------------

    // salva o endereço geográfico do candidato
    public Geografica salvarGeografica(Geografica geografica) {
        String sql = "INSERT INTO GEOGRAFICA (entidade_id, logradouro, numero, bairro, cidade, estado, cep, complemento) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // usa o id do candidato como fk
            stmt.setInt(1, geografica.getCandidato().getId());
            stmt.setString(2, geografica.getLogradouro());
            stmt.setString(3, geografica.getNumero());
            stmt.setString(4, geografica.getBairro());
            stmt.setString(5, geografica.getCidade());
            stmt.setString(6, geografica.getEstado());
            stmt.setString(7, geografica.getCep());
            stmt.setString(8, geografica.getComplemento());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        geografica.setId(rs.getInt(1));
                        return geografica;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("erro ao persistir endereço geografica: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // salva um telefone de contato
    public Fone salvarFone(Fone fone) {
        String sql = "INSERT INTO FONE (entidade_id, tipo, numero_completo, ddd, ddi) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // usa o id do candidato como fk
            stmt.setInt(1, fone.getCandidato().getId());
            stmt.setString(2, fone.getTipo());
            stmt.setString(3, fone.getNumeroCompleto());
            stmt.setString(4, fone.getDdd());
            stmt.setString(5, fone.getDdi());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        fone.setId(rs.getInt(1));
                        return fone;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("erro ao persistir fone: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // salva um contato virtual (site, rede social)
    public Virtual salvarVirtual(Virtual virtual) {
        // atenção: uso das crases (backticks) para 'virtual', pois pode ser uma palavra reservada sql
        String sql = "INSERT INTO `VIRTUAL` (entidade_id, tipo, contato) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            // usa o id do candidato como fk
            stmt.setInt(1, virtual.getCandidato().getId());
            stmt.setString(2, virtual.getTipo());
            stmt.setString(3, virtual.getContato());
            
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        virtual.setId(rs.getInt(1));
                        return virtual;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("erro ao persistir virtual: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // ---------------------- perfil e termo (passo 4 e 5) ----------------------

    // salva o perfil, habilidades e interesses em batch
    public Perfil salvarPerfil(Perfil perfil) {
        String sqlPerfil = "INSERT INTO Perfil (candidato_id, descricao) VALUES (?, ?)";
        String sqlHabilidade = "INSERT INTO Habilidade (perfil_id, nome) VALUES (?, ?)";
        String sqlInteresse = "INSERT INTO Interesse (perfil_id, nome) VALUES (?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtPerfil = conn.prepareStatement(sqlPerfil, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement stmtHabilidade = conn.prepareStatement(sqlHabilidade);
             PreparedStatement stmtInteresse = conn.prepareStatement(sqlInteresse)) {

            // 1. insere o perfil principal
            stmtPerfil.setInt(1, perfil.getCandidato().getId());
            stmtPerfil.setString(2, perfil.getDescricao());
            stmtPerfil.executeUpdate();

            try (ResultSet rs = stmtPerfil.getGeneratedKeys()) {
                if (rs.next()) {
                    int perfilIdGerado = rs.getInt(1);
                    perfil.setId(perfilIdGerado);
                    
                    // 2. insere as habilidades em lote (batch)
                    if (perfil.getHabilidades() != null) {
                        for (String habilidade : perfil.getHabilidades()) {
                            stmtHabilidade.setInt(1, perfilIdGerado);
                            stmtHabilidade.setString(2, habilidade);
                            stmtHabilidade.addBatch(); 
                        }
                        stmtHabilidade.executeBatch(); 
                    }
                    
                    // 3. insere os interesses em lote (batch)
                    if (perfil.getInteresses() != null) {
                        for (String interesse : perfil.getInteresses()) {
                            stmtInteresse.setInt(1, perfilIdGerado);
                            stmtInteresse.setString(2, interesse);
                            stmtInteresse.addBatch(); 
                        }
                        stmtInteresse.executeBatch(); 
                    }

                    return perfil;
                }
            }
        } catch (SQLException e) {
            System.err.println("erro ao persistir perfil, habilidades ou interesses: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    // registra a aceitação do termo de compromisso
    public boolean salvarAceitacaoTermo(Integer candidatoId) {
        String sqlTermo = "INSERT INTO Termo (candidato_id, data_aceitacao) VALUES (?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtTermo = conn.prepareStatement(sqlTermo)) {
            
            // registra o timestamp da aceitação
            Timestamp dataAtual = Timestamp.valueOf(LocalDateTime.now());
            
            stmtTermo.setInt(1, candidatoId);
            stmtTermo.setTimestamp(2, dataAtual);
            
            return stmtTermo.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("erro ao persistir a aceitação do termo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    // --- métodos uc003: login ---

    // busca o id da entidade pelo email (login) e senha (hash)
    public Integer buscarUsuarioPorLogin(String login, String senhaHash) {
        String sql = "SELECT id FROM Entidade WHERE email = ? AND senha_hash = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            stmt.setString(2, senhaHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            System.err.println("erro de persistência ao buscar usuário para login: " + e.getMessage());
        }
        return null;
    }
}