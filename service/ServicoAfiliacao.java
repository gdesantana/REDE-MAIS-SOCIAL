package br.com.redemaissocial.service;

import br.com.redemaissocial.dao.CandidatoDAO;
import br.com.redemaissocial.domain.Candidato;
import br.com.redemaissocial.domain.Perfil;
import br.com.redemaissocial.domain.Geografica;
import br.com.redemaissocial.domain.Fone;
import br.com.redemaissocial.domain.Virtual;
import br.com.redemaissocial.domain.Formacao; 
import java.util.regex.Pattern;

// classe responsável pela lógica de negócio dos casos de uso (afiliação) e (login).

public class ServicoAfiliacao {

    // o DAO é usado para persistir os dados no banco
    private final CandidatoDAO candidatoDAO;
    // hash de senha fixa para fins de teste
    private static final String SENHA_PADRAO_HASH = "SENHA_PADRAO_123"; 
    
    // construtor que inicializa o dao
    public ServicoAfiliacao() {
        this.candidatoDAO = new CandidatoDAO();
    }
    
    // lógica de validação 
    
    // verifica se o documento tem o tamanho correto
    private boolean validarDocumento(String documento) {
        String docLimpo = documento.replaceAll("[^0-9]", "");
        if (docLimpo.length() == 11 || docLimpo.length() == 14) {
            return true;
        }
        System.err.println("validação falhou: documento deve ter 11 (cpf) ou 14 (cnpj) dígitos.");
        return false;
    }

    // verifica se o email está em um formato básico válido
    private boolean validarEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null || !pat.matcher(email).matches()) {
            System.err.println("validação falhou: email inválido.");
            return false;
        }
        return true;
    }
    
    // lógica de negócio do fluxo de afiliação

    // verifica a duplicidade do documento 
    public Candidato iniciarAfiliacao(String documento) {
        return candidatoDAO.buscarPorDocumento(documento);
    }

    //processa os dados de identificação e persiste entidade, pessoafisica e pessoajuridica
    public Candidato processarIdentificacao(Candidato candidato) {
        
        // validações de entrada
        if (!validarDocumento(candidato.getDocumento())) {
            return null;
        }
        if (!validarEmail(candidato.getEmail())) {
            return null;
        }
        
        // decide se é PF ou PJ e chama o método de persistência correto
        String docLimpo = candidato.getDocumento().replaceAll("[^0-9]", "");
        if (docLimpo.length() == 11) {
            // PF
            return candidatoDAO.salvarCandidatoPF(candidato);
        } else if (docLimpo.length() == 14) {
            // PJ
            return candidatoDAO.salvarCandidatoPJ(candidato);
        }
        
        return null;
    }

    // processa e salva localização 
    public boolean processarLocalizacao(Candidato candidato) {
        boolean sucesso = true;
        
        if (candidato.getGeograficas() != null && !candidato.getGeograficas().isEmpty()) {
            if (!candidatoDAO.salvarGeograficas(candidato.getId(), candidato.getGeograficas())) {
                sucesso = false;
            }
        } else {
             System.out.println("log: nenhuma geográfica fornecida.");
        }
        
        if (candidato.getFones() != null && !candidato.getFones().isEmpty()) {
            if (!candidatoDAO.salvarFones(candidato.getId(), candidato.getFones())) {
                sucesso = false;
            }
        } else {
             System.out.println("log: nenhum fone fornecido.");
        }
        
        if (candidato.getVirtuais() != null && !candidato.getVirtuais().isEmpty()) {
            if (!candidatoDAO.salvarVirtuais(candidato.getId(), candidato.getVirtuais())) {
                sucesso = false;
            }
        } else {
             System.out.println("log: nenhum virtual fornecido.");
        }
        
        return sucesso;
    }
    
    //processa a formação acadêmica
    public boolean processarFormacao(Candidato candidato) {
        
        // checa se há formações para salvar
        if (candidato.getFormacoes() == null || candidato.getFormacoes().isEmpty()) {
            System.out.println("log: nenhuma formação fornecida, pulando a persistência.");
            return true;
        }
        
        // persiste a lista de formações no banco
        if (candidatoDAO.salvarFormacoes(candidato.getId(), candidato.getFormacoes())) {
            System.out.println("sucesso: formações salvas no banco.");
            return true;
        }
        
        System.err.println("erro: falha ao salvar formações no banco de dados.");
        return false;
    }
    
    // processa perfil 
    public Integer processarPerfil(Integer candidatoId, Perfil perfil) {
        
        // validação simples: verifica se o objeto perfil não é nulo
        if (perfil == null || perfil.getDescricao() == null || perfil.getDescricao().trim().isEmpty()) {
            System.err.println("validação falhou: perfil e descrição são obrigatórios.");
            return null;
        }
        
        // persiste o perfil e suas associações
        Integer novoPerfilId = candidatoDAO.salvarPerfil(candidatoId, perfil);
        
        if (novoPerfilId != null) {
            System.out.println("sucesso: perfil salvo com id: " + novoPerfilId);
            return novoPerfilId;
        }
        
        System.err.println("erro: falha ao salvar o perfil, habilidades ou interesses no banco de dados.");
        return null;
    }
    
    // processa aceitação do termo
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
    
    // métodos de login

    // tenta realizar o login usando email e hash de senha
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