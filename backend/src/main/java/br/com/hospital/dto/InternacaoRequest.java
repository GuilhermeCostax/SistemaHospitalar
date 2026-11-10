package br.com.hospital.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

/** Se dataEntrada nao for enviada, usamos a data de hoje. */
public record InternacaoRequest(
        @NotNull Long pacienteId,
        @NotNull Long medicoId,
        @NotNull Long quartoId,
        LocalDate dataEntrada,
        LocalDate previsaoAlta,
        String observacoes) {
}
