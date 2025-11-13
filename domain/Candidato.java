package br.com.redemaissocial.domain;

import java.time.LocalDate;
import java.util.List; 

// classe principal que representa o usuário do sistema (entidade), unificando os atributos de pessoa física (pf) e pessoa jurídica (pj).
 
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
    
    // atributos de pessoa física (pf) - serão nulos para pj
    private LocalDate dataNascimento; 
    private String sexo; 
    private List<Identidade> identidades; 
    
    // atributos de relação 
 
    private Perfil perfil;
    // lista de endereços geográficos 
    private List<Geografica> geograficas;
    // lista de telefones de contato 
    private List<Fone> fones;
    // lista de contatos virtuais 
    private List<Virtual> virtuais;
    // lista de formações 
    private List<Formacao> formacoes; 
    
    // construtor vazio 
    public Candidato() {
    }

    // getters e setters de identificação 
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    // getters e setters de pessoa física (pf) 
    public LocalDate getDataNascimento() { return dataNascimento; }
    public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
    
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    
    public List<Identidade> getIdentidades() { return identidades; }
    public void setIdentidades(List<Identidade> identidades) { this.identidades = identidades; }

    // getters e setters de pessoa jurídica (pj)
    public String getRazaoSocial() { return razaoSocial; }
    public void setRazaoSocial(String razaoSocial) { this.razaoSocial = razaoSocial; }
    
    public String getNomeFantasia() { return nomeFantasia; }
    public void setNomeFantasia(String nomeFantasia) { this.nomeFantasia = nomeFantasia; }
    
    public String getCnae() { return cnae; }
    public void setCnae(String cnae) { this.cnae = cnae; }
    
    public String getNaturezaJuridica() { return naturezaJuridica; }
    public void setNaturezaJuridica(String naturezaJuridica) { this.naturezaJuridica = naturezaJuridica; }

    // getters e setters de relação 
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    
    public List<Geografica> getGeograficas() { return geograficas; }
    public void setGeograficas(List<Geografica> geograficas) { this.geograficas = geograficas; }
    
    public List<Fone> getFones() { return fones; }
    public void setFones(List<Fone> fones) { this.fones = fones; }
    
    public List<Virtual> getVirtuais() { return virtuais; }
    public void setVirtuais(List<Virtual> virtuais) { this.virtuais = virtuais; }

    public List<Formacao> getFormacoes() { return formacoes;}
    public void setFormacoes(List<Formacao> formacoes) {this.formacoes = formacoes;}
}