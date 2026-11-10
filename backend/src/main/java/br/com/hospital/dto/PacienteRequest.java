package br.com.hospital.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record PacienteRequest(
        @NotBlank String nome,
        @NotBlank String cpf,
        @NotNull @Past LocalDate dataNascimento,
        String telefone,
        String email,
        String endereco) {
}
