package br.com.redemaissocial.service;

import br.com.redemaissocial.dao.CandidatoDAO;
import br.com.redemaissocial.domain.Candidato;
import br.com.redemaissocial.domain.Perfil;
import br.com.redemaissocial.domain.Geografica;
import br.com.redemaissocial.domain.Fone;
import br.com.redemaissocial.domain.Virtual;
import java.util.regex.Pattern;

/**
 * classe responsável pela lógica de negócio dos casos de uso uc002 (afiliação) e uc003 (login).
 */
public class ServicoAfiliacao {

    // o dao é usado para persistir os dados no banco
    private final CandidatoDAO candidatoDAO;
    // hash de senha fixa para fins de teste
    private static final String SENHA_PADRAO_HASH = "SENHA_PADRAO_123"; 
    
    // construtor que inicializa o dao
    public ServicoAfiliacao() {
        this.candidatoDAO = new CandidatoDAO();
    }
    
    // --- lógica de validação (robustez) ---
    
    // verifica se o documento tem o tamanho correto (11 para cpf, 14 para cnpj)
    private boolean validarDocumento(String documento) {
        String docLimpo = documento.replaceAll("[^0-9]", "");
        if (docLimpo.length() == 11 || docLimpo.length() == 14) {
            return true;
        }
        System.err.println("validação falhou: documento deve ter 11 (cpf) ou 14 (cnpj) dígitos.");
        return false;
    }
    
    // verifica se o email segue um padrão básico (regex)
    private boolean validarEmail(String email) {
        // regex básica para e-mail
        return Pattern.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", email);
    }
    
    // --- métodos uc002: solicita afiliação ---

    /** passo 1: inicia a afiliação verificando duplicidade (fluxo alternativo). */
    public Candidato iniciarAfiliacao(String documento) {
        // busca o candidato pelo cpf ou cnpj no banco
        Candidato candidatoExistente = candidatoDAO.buscarPorDocumento(documento);

        // se existir, aciona o fluxo alternativo (retorna o objeto e impede o cadastro)
        if (candidatoExistente != null) {
            System.err.println("fluxo alternativo acionado: candidato com o documento " + documento + " já existe.");
            return candidatoExistente;
        }
        System.out.println("verificado: candidato novo. prosseguindo...");
        return null; 
    }
    
    /** passo 2: processa a identificação (nome, doc, email, senha). */
    public Candidato processarIdentificacao(Candidato candidato) {
        
        // checa validações de negócio
        if (!validarDocumento(candidato.getDocumento()) || !validarEmail(candidato.getEmail())) {
            System.err.println("erro: identificação inválida.");
            return null;
        }
        
        String docLimpo = candidato.getDocumento().replaceAll("[^0-9]", "");
        Candidato novoCandidato = null;
        
        // decide se salva como pessoa física (11 dígitos)
        if (docLimpo.length() == 11) {
            novoCandidato = candidatoDAO.salvarCandidatoPF(candidato, SENHA_PADRAO_HASH);
        // decide se salva como pessoa jurídica (14 dígitos)
        } else if (docLimpo.length() == 14) {
            novoCandidato = candidatoDAO.salvarCandidatoPJ(candidato, SENHA_PADRAO_HASH);
        }
        
        // verifica se a persistência no dao foi bem sucedida
        if (novoCandidato != null && novoCandidato.getId() != null) {
            System.out.println("sucesso: novo candidato salvo com id: " + novoCandidato.getId());
            System.out.println("prosseguindo para a próxima etapa: localização e contato.");
            return novoCandidato;
        }
        
        System.err.println("erro: falha ao salvar o novo candidato no banco de dados.");
        return null;
    }

    /** passo 3: processa localização (geografica, fone, virtual). */
    public boolean processarLocalizacao(Geografica geografica, Fone fone, Virtual virtual) {
        
        // salva endereço e verifica se deu erro
        System.out.println("log: processando endereço geografica...");
        if (candidatoDAO.salvarGeografica(geografica) == null) { 
            System.err.println("erro: falha ao salvar geografica."); return false; 
        }
        
        // salva telefone e verifica se deu erro
        System.out.println("log: processando fone...");
        if (candidatoDAO.salvarFone(fone) == null) { 
            System.err.println("erro: falha ao salvar fone."); return false; 
        }
        
        // salva contato virtual e verifica se deu erro
        System.out.println("log: processando contato virtual...");
        if (candidatoDAO.salvarVirtual(virtual) == null) { 
            System.err.println("erro: falha ao salvar virtual."); return false; 
        }
        
        System.out.println("sucesso: localização e contato salvos.");
        System.out.println("prosseguindo para a próxima etapa: perfil.");
        return true;
    }

    /** passo 4: processa perfil (habilidades e interesses). */
    public Perfil processarPerfil(Perfil perfil) {
        
        // salva o perfil e seus itens relacionados (habilidade/interesse)
        Perfil novoPerfil = candidatoDAO.salvarPerfil(perfil);
        
        if (novoPerfil != null && novoPerfil.getId() != null) {
            System.out.println("sucesso: perfil e relacionados salvos com id: " + novoPerfil.getId());
            System.out.println("prosseguindo para a próxima etapa: termo.");
            return novoPerfil;
        }
        
        System.err.println("erro: falha ao salvar o perfil, habilidades ou interesses no banco de dados.");
        return null;
    }
    
    /** passo 5: processa aceitação do termo. */
    public boolean processarTermo(Candidato candidato) {
        
        // registra a aceitação do termo final
        if (candidatoDAO.salvarAceitacaoTermo(candidato.getId())) {
            System.out.println("sucesso: termo de compromisso registrado.");
            System.out.println("afiliação concluída! candidato pode acessar o sistema.");
            return true;
        }
        
        System.err.println("erro: falha ao registrar a aceitação do termo.");
        return false;
    }
    
    // --- métodos uc003: login ---

    /** tenta realizar o login usando email e hash de senha. */
    public Integer realizarLogin(String login, String senha) {
        // usa o hash de senha padrão para a busca
        String senhaHash = SENHA_PADRAO_HASH; 
        
        System.out.println("log: tentativa de login para: " + login);

        // busca o id da entidade no banco com o email e hash fornecidos
        Integer entidadeId = candidatoDAO.buscarUsuarioPorLogin(login, senhaHash);

        if (entidadeId != null) {
            System.out.println("sucesso: login realizado. entidade id: " + entidadeId);
            return entidadeId;
        } else {
            System.err.println("erro: login ou senha inválidos.");
            return null;
        }
    }
}