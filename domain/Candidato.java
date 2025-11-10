package br.com.redemaissocial.domain;

import java.util.List; // import necessário para listas (perfis, fones, geograficas)

/**
 * classe principal que representa o usuário do sistema (entidade),
 * unificando os atributos de pessoa física (pf) e pessoa jurídica (pj).
 */
public class Candidato {
    
    // id único na tabela 'entidade' (chave primária)
    private Integer id;
    // nome da pessoa (pf) ou nome fantasia (pj)
    private String nome;
    // campo genérico que armazena cpf (11 dígitos) ou cnpj (14 dígitos)
    private String documento; 
    // email do usuário (usado como login)
    private String email;
    
    // atributos de pessoa jurídica (pj) - serão nulos para pf
    private String razaoSocial;
    private String nomeFantasia; // pode ser usado como nome em 'entidade'
    private String cnae;
    private String naturezaJuridica;
    
    // atributos de relação (opcionalmente usados em daos para buscar dados completos)
    // perfil de habilidades e interesses (passo 4)
    private Perfil perfil;
    // lista de endereços geográficos (passo 3)
    private List<Geografica> geograficas;
    // lista de telefones de contato (passo 3)
    private List<Fone> fones;
    
    // construtor vazio (necessário para java beans)
    public Candidato() {
    }

    // --- getters e setters de identificação ---
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // --- getters e setters de pessoa jurídica (pj) ---
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    
    public String getCnae() { return cnae; }
    public void setCnae(String cnae) { this.cnae = cnae; }
    
    public String getNaturezaJuridica() { return naturezaJuridica; }
    public void setNaturezaJuridica(String naturezaJuridica) { this.naturezaJuridica = naturezaJuridica; }

    // --- getters e setters de relação (opcional, para modelo completo) ---
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    
    public List<Geografica> getGeograficas() { return geograficas; }
    public void setGeograficas(List<Geografica> geograficas) { this.geograficas = geograficas; }
    
    public List<Fone> getFones() { return fones; }
    public void setFones(List<Fone> fones) { this.fones = fones; }
}