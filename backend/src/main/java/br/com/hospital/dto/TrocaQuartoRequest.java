package br.com.hospital.dto;

import jakarta.validation.constraints.NotNull;

public record TrocaQuartoRequest(@NotNull Long quartoId) {
}
