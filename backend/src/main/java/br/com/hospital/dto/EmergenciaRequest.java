package br.com.hospital.dto;

import br.com.hospital.enums.PrioridadeEmergencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmergenciaRequest(
        @NotNull Long pacienteId,
        @NotNull PrioridadeEmergencia prioridade,
        @NotBlank String queixa) {
}
