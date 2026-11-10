package br.com.hospital.dto;

import jakarta.validation.constraints.NotNull;

public record IniciarEmergenciaRequest(@NotNull Long medicoId) {
}
