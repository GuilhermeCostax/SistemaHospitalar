package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.hospital.exception.RegraNegocioException;

/**
 * Classe base das modalidades de atendimento. Guarda o que e comum
 * (paciente, data, hora, observacoes) e obriga cada modalidade a
 * implementar a propria validacao.
 */
public abstract class Atendimento extends Entidade {

    protected Paciente paciente;
    protected LocalDate data;
    protected LocalTime hora;
    protected String observacoes;

    public Atendimento(Paciente paciente, LocalDate data, LocalTime hora) {
        this.paciente = paciente;
        this.data = data;
        this.hora = hora;
    }

    /** Cada modalidade verifica suas proprias condicoes e lanca RegraNegocioException se algo estiver errado. */
    public abstract void validarCondicoes();

    @JsonProperty("modalidade")
    public abstract String getModalidade();

    // validacoes que todas as modalidades usam
    protected void validarCamposComuns() {
        if (paciente == null) {
            throw new RegraNegocioException("O atendimento precisa de um paciente.");
        }
        if (data == null || hora == null) {
            throw new RegraNegocioException("O atendimento precisa de data e horário.");
        }
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public LocalDate getData() {
        return data;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getObservacoes() {
        return observacoes;
    }
}
