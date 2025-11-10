package br.com.redemaissocial.domain;

public class Geografica {
    // id do registro na tabela 'geografica'
    private Integer id;
    // chave estrangeira (fk): liga este endereço ao candidato proprietário
    private Candidato candidato; 
    // nome da rua ou avenida
    private String logradouro;
    // número do imóvel
    private String numero;
    // nome do bairro
    private String bairro;
    // nome da cidade
    private String cidade;
    // sigla do estado (ex: 'sp', 'rj')
    private String estado;
    // código de endereçamento postal
    private String cep;
    // informações adicionais (ex: 'apartamento 10', 'fundos')
    private String complemento;

    // getters e setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Candidato getCandidato() { return candidato; }
    public void setCandidato(Candidato candidato) { this.candidato = candidato; }
    public String getLogradouro() { return logradouro; }
    public void setLogradouro(String logradouro) { this.logradouro = logradouro; }
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }
    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
}