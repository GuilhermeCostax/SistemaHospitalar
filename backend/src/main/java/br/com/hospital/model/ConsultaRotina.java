package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.hospital.exception.RegraNegocioException;

public class ConsultaRotina extends Consulta {

    public ConsultaRotina(Paciente paciente, Profissional medico, LocalDate data, LocalTime hora, String motivo) {
        super(paciente, medico, data, hora, motivo);
    }

    @Override
    public void validarCondicoes() {
        super.validarCondicoes();
        // consulta de rotina e feita pelo clinico geral
        if (!(medico instanceof ClinicoGeral)) {
            throw new RegraNegocioException("Consulta de rotina deve ser realizada por um clínico geral.");
        }
    }

    @Override
    protected Consulta criarNovoAgendamento(LocalDate novaData, LocalTime novaHora) {
        return new ConsultaRotina(paciente, medico, novaData, novaHora, motivo);
    }

    @Override
    public String getModalidade() {
        return "CONSULTA_ROTINA";
    }
}
