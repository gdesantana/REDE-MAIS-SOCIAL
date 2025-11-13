package br.com.redemaissocial.domain;

import java.util.List;

//classe que representa o perfil profissional do candidato (passo 4 da afiliação), incluindo descrição, habilidades e interesses.

public class Perfil {
    // id do registro na tabela 'perfil' (PK)
    private Integer id; 
    
    // chave estrangeira (FK)
    private Candidato candidato; 

    // descrição resumida das qualificações ou objetivos de carreira
    private String descricao;
    
    // lista de strings para simplificar a passagem de dados de habilidades
    private List<String> habilidades;
    // lista de strings para simplificar a passagem de dados de interesses
    private List<String> interesses;

    // construtor
    public Perfil() {}

    // getters e setters
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Candidato getCandidato() {
        return candidato;
    }

    public void setCandidato(Candidato candidato) {
        this.candidato = candidato;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<String> habilidades) {
        this.habilidades = habilidades;
    }

    public List<String> getInteresses() {
        return interesses;
    }

    public void setInteresses(List<String> interesses) {
        this.interesses = interesses;
    }
}