package br.com.redemaissocial.domain;

public class Virtual {
    // id do registro na tabela 'virtual' (PK)
    private Integer id;
    // chave estrangeira (FK)
    private Candidato candidato;
    // tipo de contato 
    private String tipo; 
    // o link ou identificador do contato
    private String contato;

    // getters e setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Candidato getCandidato() { return candidato; }
    public void setCandidato(Candidato candidato) { this.candidato = candidato; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getContato() { return contato; }
    public void setContato(String contato) { this.contato = contato; }
}