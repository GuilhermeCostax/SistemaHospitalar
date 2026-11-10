package br.com.hospital.dto;

import br.com.hospital.enums.TipoQuarto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record QuartoRequest(
        @NotBlank String numero,
        String andar,
        @NotNull TipoQuarto tipo,
        @NotNull Integer capacidade) {
}
