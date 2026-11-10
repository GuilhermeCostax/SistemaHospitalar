package br.com.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.hospital.enums.Especialidade;
import jakarta.validation.constraints.NotNull;

/** Dados para o clinico geral encaminhar o paciente a um especialista. */
public record EncaminhamentoRequest(
        @NotNull Especialidade especialidade,
        @NotNull Long medicoId,
        @NotNull LocalDate data,
        @NotNull LocalTime hora,
        String motivo) {
}
