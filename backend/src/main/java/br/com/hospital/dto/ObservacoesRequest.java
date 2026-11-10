package br.com.hospital.dto;

import jakarta.validation.constraints.NotBlank;

public record ObservacoesRequest(@NotBlank String observacoes) {
}
