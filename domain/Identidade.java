package br.com.redemaissocial.domain;

// classe que representa documentos de identificação secundário. Mapeia para a tabela Identidade.

public class Identidade {

    private Integer id;
    // chave estrangeira (FK): liga este documento ao candidato proprietário
    private Candidato candidato; 
    private String tipoDocumento; 
    private String valorDocumento;
    private String dataEmissao; 
    public Identidade() {
    }

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
    
    public String getTipoDocumento() { 
        return tipoDocumento; 
    }
    public void setTipoDocumento(String tipoDocumento) { 
        this.tipoDocumento = tipoDocumento; 
    }
    
    public String getValorDocumento() { 
        return valorDocumento; 
    }
    public void setValorDocumento(String valorDocumento) { 
        this.valorDocumento = valorDocumento; 
    }
    
    public String getDataEmissao() { 
        return dataEmissao; 
    }
    public void setDataEmissao(String dataEmissao) { 
        this.dataEmissao = dataEmissao; 
    }
}