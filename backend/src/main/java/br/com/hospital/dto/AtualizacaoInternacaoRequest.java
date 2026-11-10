package br.com.hospital.dto;

import java.time.LocalDate;

/** Campos de uma internacao que podem ser alterados enquanto ela esta em andamento. */
public record AtualizacaoInternacaoRequest(
        LocalDate previsaoAlta,
        String observacoes) {
}
