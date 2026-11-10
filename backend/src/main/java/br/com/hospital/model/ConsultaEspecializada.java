package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.hospital.enums.Especialidade;
import br.com.hospital.exception.RegraNegocioException;

public class ConsultaEspecializada extends Consulta {

    private Especialidade especialidade;

    public ConsultaEspecializada(Paciente paciente, Profissional medico, LocalDate data, LocalTime hora,
                                 String motivo, Especialidade especialidade) {
        super(paciente, medico, data, hora, motivo);
        this.especialidade = especialidade;
    }

    @Override
    public void validarCondicoes() {
        super.validarCondicoes();
        if (especialidade == null) {
            throw new RegraNegocioException("A consulta especializada precisa de uma especialidade.");
        }
        // a especialidade do medico precisa bater com a da consulta
        if (!(medico instanceof MedicoEspecialista)) {
            throw new RegraNegocioException("Consulta especializada deve ser realizada por um médico especialista.");
        }
        MedicoEspecialista especialista = (MedicoEspecialista) medico;
        if (especialista.getEspecialidade() != especialidade) {
            throw new RegraNegocioException("O médico é especialista em "
                    + especialista.getEspecialidade().getDescricao()
                    + " e não pode atender " + especialidade.getDescricao() + ".");
        }
    }

    @Override
    public String getModalidade() {
        return "CONSULTA_ESPECIALIZADA";
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }
}
