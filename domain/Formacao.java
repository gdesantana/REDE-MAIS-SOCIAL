package br.com.redemaissocial.domain;

// classe que representa a formação acadêmica ou profissional do candidato.
 
public class Formacao {
    private Integer id;
    // chave Estrangeira (FK): id do candidato proprietário
    private Candidato candidato; 
    private String curso;
    private String tipo; 

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Candidato getCandidato() { return candidato; }
    public void setCandidato(Candidato candidato) { this.candidato = candidato; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}