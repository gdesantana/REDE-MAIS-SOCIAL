package br.com.redemaissocial.dao;

import br.com.redemaissocial.domain.Candidato;
import br.com.redemaissocial.domain.Perfil;
import br.com.redemaissocial.domain.Geografica;
import br.com.redemaissocial.domain.Fone;
import br.com.redemaissocial.domain.Virtual;
import br.com.redemaissocial.domain.Identidade; 
import br.com.redemaissocial.domain.Formacao; 
import br.com.redemaissocial.infra.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Date; 
import java.time.LocalDateTime;
import java.util.List;

public class CandidatoDAO {

    private static final String SENHA_PADRAO_HASH = "SENHA_PADRAO_123";

    // métodos auxiliares e de identificação

    // busca um candidato pelo cpf ou cnpj para checagem de duplicidade
    public Candidato buscarPorDocumento(String documento) {
        String sql = "SELECT E.ID, E.NOME, PF.CPF, PJ.CNPJ " +
                     "FROM Entidade E " + 
                     "LEFT JOIN PessoaFisica PF ON E.ID = PF.ID " + 
                     "LEFT JOIN PessoaJuridica PJ ON E.ID = PJ.ID " + 
                     "WHERE PF.CPF = ? OR PJ.CNPJ = ?";
        
        String docLimpo = documento.replaceAll("[^0-9]", "");
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, docLimpo);
            stmt.setString(2, docLimpo); 

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Candidato c = new Candidato();
                    c.setId(rs.getInt("ID"));
                    c.setNome(rs.getString("NOME"));
                    String cpf = rs.getString("CPF");
                    String cnpj = rs.getString("CNPJ");
                    c.setDocumento(cpf != null ? cpf : cnpj); 
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("erro de persistência ao buscar documento: " + e.getMessage());
        }
        return null;
    }
    
    // persiste um novo candidato PF (entidade, candidato e pessoafisica)
    public Candidato salvarCandidatoPF(Candidato candidato) {
        String sqlEntidade = "INSERT INTO Entidade (NOME, EMAIL, SENHA_HASH) VALUES (?, ?, ?)";
        String sqlCandidato = "INSERT INTO Candidato (ID) VALUES (?)";
        String sqlPF = "INSERT INTO PessoaFisica (ID, CPF, DATA_NASCIMENTO, SEXO) VALUES (?, ?, ?, ?)";
        
        String cpfLimpo = candidato.getDocumento().replaceAll("[^0-9]", "");
        
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); 
            int idEntidade = 0;
            
            // persiste Entidade
            try (PreparedStatement stmtEntidade = conn.prepareStatement(sqlEntidade, Statement.RETURN_GENERATED_KEYS)) {
                stmtEntidade.setString(1, candidato.getNome());
                stmtEntidade.setString(2, candidato.getEmail());
                stmtEntidade.setString(3, SENHA_PADRAO_HASH);
                
                stmtEntidade.executeUpdate();
                
                try (ResultSet rs = stmtEntidade.getGeneratedKeys()) {
                    if (rs.next()) {
                        idEntidade = rs.getInt(1);
                        candidato.setId(idEntidade); 
                    }
                }
            }

            // persiste Candidato
            try (PreparedStatement stmtCandidato = conn.prepareStatement(sqlCandidato)) {
                stmtCandidato.setInt(1, idEntidade);
                stmtCandidato.executeUpdate();
            }

            // persiste PessoaFisica
            try (PreparedStatement stmtPF = conn.prepareStatement(sqlPF)) {
                stmtPF.setInt(1, idEntidade);
                stmtPF.setString(2, cpfLimpo);
                stmtPF.setDate(3, Date.valueOf(candidato.getDataNascimento()));
                stmtPF.setString(4, candidato.getSexo()); 
                stmtPF.executeUpdate();
            }
            
            // persiste Identidades
            if (candidato.getIdentidades() != null && !candidato.getIdentidades().isEmpty()) {
                salvarIdentidades(idEntidade, candidato.getIdentidades(), conn);
            }
            
            conn.commit(); 
            System.out.println("sucesso: novo candidato pf salvo com id: " + idEntidade);
            return candidato;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("erro ao tentar rollback: " + ex.getMessage());
            }
            System.err.println("erro fatal ao persistir candidato pf: " + e.getMessage());
            return null;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                 System.err.println("erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
    
    //persiste um novo candidato PJ (entidade e pessoajuridica)
    public Candidato salvarCandidatoPJ(Candidato candidato) {
        String sqlEntidade = "INSERT INTO Entidade (NOME, EMAIL, SENHA_HASH) VALUES (?, ?, ?)";
        String sqlCandidato = "INSERT INTO Candidato (ID) VALUES (?)";
        String sqlPJ = "INSERT INTO PessoaJuridica (ID, CNPJ, RAZAO_SOCIAL, NOME_FANTASIA, CNAE, NATUREZA_JURIDICA) VALUES (?, ?, ?, ?, ?, ?)";
        
        String cnpjLimpo = candidato.getDocumento().replaceAll("[^0-9]", "");
        
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); 
            int idEntidade = 0;
            
            // persiste Entidade 
            try (PreparedStatement stmtEntidade = conn.prepareStatement(sqlEntidade, Statement.RETURN_GENERATED_KEYS)) {
                String nomeParaEntidade = candidato.getNomeFantasia() != null ? candidato.getNomeFantasia() : candidato.getRazaoSocial();
                stmtEntidade.setString(1, nomeParaEntidade); 
                stmtEntidade.setString(2, candidato.getEmail());
                stmtEntidade.setString(3, SENHA_PADRAO_HASH);
                
                stmtEntidade.executeUpdate();
                
                try (ResultSet rs = stmtEntidade.getGeneratedKeys()) {
                    if (rs.next()) {
                        idEntidade = rs.getInt(1);
                        candidato.setId(idEntidade); 
                    }
                }
            }

            // persiste Candidato
            try (PreparedStatement stmtCandidato = conn.prepareStatement(sqlCandidato)) {
                stmtCandidato.setInt(1, idEntidade);
                stmtCandidato.executeUpdate();
            }

            // persiste PessoaJuridica
            try (PreparedStatement stmtPJ = conn.prepareStatement(sqlPJ)) {
                stmtPJ.setInt(1, idEntidade);
                stmtPJ.setString(2, cnpjLimpo);
                stmtPJ.setString(3, candidato.getRazaoSocial());
                stmtPJ.setString(4, candidato.getNomeFantasia());
                stmtPJ.setString(5, candidato.getCnae());
                stmtPJ.setString(6, candidato.getNaturezaJuridica());
                stmtPJ.executeUpdate();
            }
            
            // persiste Identidades
             if (candidato.getIdentidades() != null && !candidato.getIdentidades().isEmpty()) {
                salvarIdentidades(idEntidade, candidato.getIdentidades(), conn);
            }

            conn.commit(); 
            System.out.println("sucesso: novo candidato pj salvo com id: " + idEntidade);
            return candidato;

        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                 System.err.println("erro ao tentar rollback: " + ex.getMessage());
            }
            System.err.println("erro fatal ao persistir candidato pj: " + e.getMessage());
            return null;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                 System.err.println("erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
    
    //persiste a lista de documentos de identificação secundários 
    private void salvarIdentidades(Integer entidadeId, List<Identidade> identidades, Connection conn) throws SQLException {
        String sql = "INSERT INTO Identidade (ENTIDADE_ID, TIPO_DOCUMENTO, VALOR_DOCUMENTO, DATA_EMISSAO) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int count = 0;
            for (Identidade identidade : identidades) {
                stmt.setInt(1, entidadeId);
                stmt.setString(2, identidade.getTipoDocumento());
                stmt.setString(3, identidade.getValorDocumento());
                stmt.setDate(4, Date.valueOf(identidade.getDataEmissao())); 
                stmt.addBatch(); 
                count++;
            }
            stmt.executeBatch(); 
            System.out.println("LOG: " + count + " identidades salvas em lote.");
        }
    }
    
    // métodos de localização, formação e perfil 

    // salva a lista de endereços geográficos 
    public boolean salvarGeograficas(Integer entidadeId, List<Geografica> geograficas) {
        String sql = "INSERT INTO GEOGRAFICA (ENTIDADE_ID, LOGRADOURO, NUMERO, CIDADE, ESTADO, CEP) VALUES (?, ?, ?, ?, ?, ?)"; 
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Geografica geo : geograficas) {
                stmt.setInt(1, entidadeId);
                stmt.setString(2, geo.getLogradouro());
                stmt.setString(3, geo.getNumero());
                stmt.setString(4, geo.getCidade());
                stmt.setString(5, geo.getEstado());
                stmt.setString(6, geo.getCep());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
            
        } catch (SQLException e) {
            System.err.println("erro ao persistir geograficas: " + e.getMessage());
            return false;
        }
    }
    
    //salva a lista de fones 
    public boolean salvarFones(Integer entidadeId, List<Fone> fones) {
        String sql = "INSERT INTO FONE (ENTIDADE_ID, TIPO, DDD, NUMERO_COMPLETO) VALUES (?, ?, ?, ?)"; 
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Fone fone : fones) {
                stmt.setInt(1, entidadeId);
                stmt.setString(2, fone.getTipo());
                stmt.setString(3, fone.getDdd());
                stmt.setString(4, fone.getNumeroCompleto());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
            
        } catch (SQLException e) {
            System.err.println("erro ao persistir fones: " + e.getMessage());
            return false;
        }
    }
    
    //salva a lista de contatos virtuais 
    public boolean salvarVirtuais(Integer entidadeId, List<Virtual> virtuais) {
        
        String sql = "INSERT INTO `VIRTUAL` (ENTIDADE_ID, TIPO, CONTATO) VALUES (?, ?, ?)"; 
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Virtual virtual : virtuais) {
                stmt.setInt(1, entidadeId);
                stmt.setString(2, virtual.getTipo());
                stmt.setString(3, virtual.getContato());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
            
        } catch (SQLException e) {
            System.err.println("erro ao persistir virtuais: " + e.getMessage());
            return false;
        }
    }
    
    //salva a lista de formações 
    public boolean salvarFormacoes(Integer candidatoId, List<Formacao> formacoes) {
      
        String sql = "INSERT INTO FORMACAO (CANDIDATO_ID, CURSO, TIPO) VALUES (?, ?, ?)"; 
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (Formacao formacao : formacoes) {
                stmt.setInt(1, candidatoId);
                stmt.setString(2, formacao.getCurso());
                stmt.setString(3, formacao.getTipo());
                stmt.addBatch();
            }
            stmt.executeBatch();
            return true;
            
        } catch (SQLException e) {
            System.err.println("erro ao persistir formações: " + e.getMessage());
            return false;
        }
    }
    
    //salva o perfil, habilidades e interesses 
    public Integer salvarPerfil(Integer candidatoId, Perfil perfil) {
        
        String sqlPerfil = "INSERT INTO PERFIL (CANDIDATO_ID, DESCRICAO) VALUES (?, ?)";
        String sqlHabilidade = "INSERT INTO PERFIL_HABILIDADE (PERFIL_ID, HABILIDADE_ID) VALUES (?, ?)";
        String sqlInteresse = "INSERT INTO PERFIL_INTERESSE (PERFIL_ID, INTERESSE_ID) VALUES (?, ?)";
        
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false);
            Integer perfilId = null;
            
            // persiste Perfil
            try (PreparedStatement stmtPerfil = conn.prepareStatement(sqlPerfil, Statement.RETURN_GENERATED_KEYS)) {
                stmtPerfil.setInt(1, candidatoId);
                stmtPerfil.setString(2, perfil.getDescricao());
                stmtPerfil.executeUpdate();
                
                try (ResultSet rs = stmtPerfil.getGeneratedKeys()) {
                    if (rs.next()) {
                        perfilId = rs.getInt(1);
                    }
                }
            }

            if (perfilId == null) {
                conn.rollback();
                return null;
            }
            
            // persiste Habilidades (simulado)
            if (perfil.getHabilidades() != null) {
                try (PreparedStatement stmtHabilidade = conn.prepareStatement(sqlHabilidade)) {
                    int habilidadeIdFicticio = 1000;
                    for (String hab : perfil.getHabilidades()) {
                        stmtHabilidade.setInt(1, perfilId);
                        stmtHabilidade.setInt(2, habilidadeIdFicticio++);
                        stmtHabilidade.addBatch();
                    }
                    stmtHabilidade.executeBatch();
                }
            }

            // persiste Interesses (simulado)
            if (perfil.getInteresses() != null) {
                try (PreparedStatement stmtInteresse = conn.prepareStatement(sqlInteresse)) {
                    int interesseIdFicticio = 2000;
                    for (String inte : perfil.getInteresses()) {
                        stmtInteresse.setInt(1, perfilId);
                        stmtInteresse.setInt(2, interesseIdFicticio++);
                        stmtInteresse.addBatch();
                    }
                    stmtInteresse.executeBatch();
                }
            }
            
            conn.commit();
            return perfilId;
            
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                 System.err.println("erro ao tentar rollback: " + ex.getMessage());
            }
            System.err.println("erro ao persistir perfil: " + e.getMessage());
            return null;
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
    
    //registra a aceitação do termo de compromisso 
    public boolean salvarAceitacaoTermo(Integer candidatoId) {
        
        String sqlTermo = "INSERT INTO TERMO (CANDIDATO_ID, DATA_ACEITACAO) VALUES (?, ?)";
        
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmtTermo = conn.prepareStatement(sqlTermo)) {
            
            Timestamp dataAtual = Timestamp.valueOf(LocalDateTime.now());
            
            stmtTermo.setInt(1, candidatoId);
            stmtTermo.setTimestamp(2, dataAtual);
            
            return stmtTermo.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("erro ao persistir a aceitação do termo: " + e.getMessage());
            return false;
        }
    }
    
    //métodos login 

    // busca o id da entidade pelo email (login) e senha (hash)
    public Integer buscarUsuarioPorLogin(String login, String senhaHash) {
        String sql = "SELECT ID FROM Entidade WHERE EMAIL = ? AND SENHA_HASH = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, login);
            stmt.setString(2, senhaHash);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ID");
                }
            }
        } catch (SQLException e) {
            System.err.println("erro de persistência ao buscar usuário para login: " + e.getMessage());
        }
        return null;
    }
}