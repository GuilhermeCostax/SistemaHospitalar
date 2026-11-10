package br.com.hospital.dto;

import br.com.hospital.enums.CategoriaProfissional;
import br.com.hospital.enums.Especialidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * "especialidade" so e usada para ESPECIALISTA e "areaCirurgica" so para CIRURGIAO.
 */
public record ProfissionalRequest(
        @NotBlank String nome,
        @NotBlank String registro,
        String telefone,
        String email,
        @NotNull CategoriaProfissional categoria,
        Especialidade especialidade,
        String areaCirurgica) {
}
