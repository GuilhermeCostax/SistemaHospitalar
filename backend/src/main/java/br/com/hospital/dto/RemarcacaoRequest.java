package br.com.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.NotNull;

/** Nova data e horario de uma consulta remarcada. */
public record RemarcacaoRequest(
        @NotNull LocalDate data,
        @NotNull LocalTime hora) {
}
