package br.com.hospital.model;

import java.time.LocalDate;

import br.com.hospital.enums.StatusInternacao;
import br.com.hospital.exception.RegraNegocioException;

public class Internacao extends Entidade {

    private Paciente paciente;
    private Profissional medico;
    private Quarto quarto;
    private LocalDate dataEntrada;
    private LocalDate previsaoAlta;
    private LocalDate dataAlta;
    private StatusInternacao status = StatusInternacao.EM_ANDAMENTO;
    private String observacoes;

    public Internacao(Paciente paciente, Profissional medico, Quarto quarto,
                      LocalDate dataEntrada, LocalDate previsaoAlta, String observacoes) {
        this.paciente = paciente;
        this.medico = medico;
        this.quarto = quarto;
        this.dataEntrada = dataEntrada;
        this.previsaoAlta = previsaoAlta;
        this.observacoes = observacoes;
    }

    public void validar() {
        if (previsaoAlta != null && previsaoAlta.isBefore(dataEntrada)) {
            throw new RegraNegocioException("A previsão de alta não pode ser anterior à data de entrada.");
        }
    }

    public void darAlta(LocalDate data) {
        if (status == StatusInternacao.ALTA_REALIZADA) {
            throw new RegraNegocioException("Esta internação já teve alta.");
        }
        if (data.isBefore(dataEntrada)) {
            throw new RegraNegocioException("A data de alta não pode ser anterior à data de entrada.");
        }
        this.dataAlta = data;
        this.status = StatusInternacao.ALTA_REALIZADA;
        quarto.liberar();
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public Profissional getMedico() {
        return medico;
    }

    public Quarto getQuarto() {
        return quarto;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public LocalDate getPrevisaoAlta() {
        return previsaoAlta;
    }

    public LocalDate getDataAlta() {
        return dataAlta;
    }

    public StatusInternacao getStatus() {
        return status;
    }

    public String getObservacoes() {
        return observacoes;
    }
}
