package br.com.redemaissocial.domain;

public class Fone {
    // id do registro na tabela 'fone' do banco
    private Integer id;
    // chave estrangeira (FK): liga este telefone ao candidato proprietário
    private Candidato candidato;
    // tipo de telefone 
    private String tipo;
    // número completo do telefone 
    private String numeroCompleto;
    // código de área 
    private String ddd;
    // código do país 
    private String ddi;

    // getters e setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Candidato getCandidato() { return candidato; }
    public void setCandidato(Candidato candidato) { this.candidato = candidato; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getNumeroCompleto() { return numeroCompleto; }
    public void setNumeroCompleto(String numeroCompleto) { this.numeroCompleto = numeroCompleto; }
    public String getDdd() { return ddd; }
    public void setDdd(String ddd) { this.ddd = ddd; }
    public String getDdi() { return ddi; }
    public void setDdi(String ddi) { this.ddi = ddi; }
}