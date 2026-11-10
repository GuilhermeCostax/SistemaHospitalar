package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;
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

    /** Verdadeiro se ha um procedimento agendado ou em andamento neste dia e horario. */
    public boolean temProcedimentoEm(LocalDate data, LocalTime hora) {
        return procedimentos.stream()
                .filter(ProcedimentoCirurgico::estaPendente)
                .anyMatch(p -> p.getData().equals(data) && p.getHora().equals(hora));
    }

    /** Verdadeiro se ainda existe algum procedimento agendado ou em andamento. */
    public boolean temProcedimentosPendentes() {
        return procedimentos.stream().anyMatch(ProcedimentoCirurgico::estaPendente);
    }

    @Override
    public String getCategoria() {
        return "CIRURGIAO";
    }
}
