package br.com.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.hospital.enums.TipoCirurgia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CirurgiaRequest(
        @NotNull Long pacienteId,
        @NotNull Long cirurgiaoId,
        @NotNull LocalDate dataPrevista,
        @NotNull LocalTime horaPrevista,
        @NotBlank String descricao,
        @NotNull TipoCirurgia tipo,
        String observacoesPreOperatorias) {
}
