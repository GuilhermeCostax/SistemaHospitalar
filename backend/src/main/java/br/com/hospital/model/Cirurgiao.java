package br.com.hospital.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cirurgiao extends Profissional {

    private String areaCirurgica;

    // procedimentos que o cirurgiao realizou / vai realizar
    @JsonIgnore
    private final List<ProcedimentoCirurgico> procedimentos = new ArrayList<>();

    public Cirurgiao(String nome, String registro, String telefone, String email, String areaCirurgica) {
        super(nome, registro, telefone, email);
        this.areaCirurgica = areaCirurgica;
    }

    public String getAreaCirurgica() {
        return areaCirurgica;
    }

    @JsonIgnore
    public List<ProcedimentoCirurgico> getProcedimentos() {
        return procedimentos;
    }

    public void registrarProcedimento(ProcedimentoCirurgico procedimento) {
        procedimentos.add(procedimento);
    }

    @Override
    public String getCategoria() {
        return "CIRURGIAO";
    }
}
