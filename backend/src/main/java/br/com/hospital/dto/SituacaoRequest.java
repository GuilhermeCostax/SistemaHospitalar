package br.com.hospital.dto;

import br.com.hospital.enums.SituacaoConsulta;
import jakarta.validation.constraints.NotNull;

public record SituacaoRequest(@NotNull SituacaoConsulta situacao) {
}
