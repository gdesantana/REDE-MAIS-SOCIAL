package br.com.redemaissocial.domain;

public class Fone {
    // id do registro na tabela 'fone' do banco
    private Integer id;
    // chave estrangeira (fk): liga este telefone ao candidato proprietário
    private Candidato candidato;
    // tipo de telefone (ex: 'celular', 'comercial', 'residencial')
    private String tipo;
    // número completo do telefone (geralmente sem ddd/ddi)
    private String numeroCompleto;
    // código de área (ex: '11', '21')
    private String ddd;
    // código do país (ex: '55' para brasil)
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