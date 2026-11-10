package br.com.hospital.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dados comuns a todos os profissionais da saude. As categorias
 * (clinico geral, especialista e cirurgiao) herdam desta classe.
 */
public abstract class Profissional extends Pessoa {

    protected String registro;

    public Profissional(String nome, String registro, String telefone, String email) {
        super(nome, telefone, email);
        this.registro = registro;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    /** Nome da categoria, aparece no JSON como "categoria". */
    @JsonProperty("categoria")
    public abstract String getCategoria();
}
