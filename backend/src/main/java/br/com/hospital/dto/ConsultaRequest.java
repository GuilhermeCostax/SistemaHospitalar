package br.com.hospital.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.hospital.enums.Especialidade;
import br.com.hospital.enums.TipoConsulta;
import jakarta.validation.constraints.NotNull;

public record ConsultaRequest(
        @NotNull TipoConsulta tipo,
        @NotNull Long pacienteId,
        @NotNull Long medicoId,
        @NotNull LocalDate data,
        @NotNull LocalTime hora,
        String motivo,
        Especialidade especialidade) {
}
