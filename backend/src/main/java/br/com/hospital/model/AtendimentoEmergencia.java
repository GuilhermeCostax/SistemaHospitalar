package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.hospital.enums.PrioridadeEmergencia;
import br.com.hospital.enums.StatusEmergencia;
import br.com.hospital.exception.RegraNegocioException;

public class AtendimentoEmergencia extends Atendimento {

    private PrioridadeEmergencia prioridade;
    private String queixa;
    private StatusEmergencia status = StatusEmergencia.AGUARDANDO_AVALIACAO;
    // o medico so e definido quando o atendimento comeca
    private Profissional medico;

    public AtendimentoEmergencia(Paciente paciente, PrioridadeEmergencia prioridade, String queixa) {
        // emergencia acontece na hora do registro
        super(paciente, LocalDate.now(), LocalTime.now().withNano(0));
        this.prioridade = prioridade;
        this.queixa = queixa;
    }

    @Override
    public void validarCondicoes() {
        validarCamposComuns();
        if (prioridade == null) {
            throw new RegraNegocioException("O atendimento de emergência precisa de uma prioridade.");
        }
        if (queixa == null || queixa.isBlank()) {
            throw new RegraNegocioException("Informe a queixa do paciente.");
        }
    }

    public void iniciar(Profissional medico) {
        if (status != StatusEmergencia.AGUARDANDO_AVALIACAO) {
            throw new RegraNegocioException("Só é possível iniciar um atendimento que está aguardando avaliação.");
        }
        if (medico == null) {
            throw new RegraNegocioException("Informe o médico que fará o atendimento.");
        }
        this.medico = medico;
        this.status = StatusEmergencia.EM_ATENDIMENTO;
    }

    public void concluir() {
        if (status != StatusEmergencia.EM_ATENDIMENTO) {
            throw new RegraNegocioException("Só é possível concluir um atendimento que foi iniciado.");
        }
        this.status = StatusEmergencia.CONCLUIDO;
    }

    @Override
    public String getModalidade() {
        return "EMERGENCIA";
    }

    public PrioridadeEmergencia getPrioridade() {
        return prioridade;
    }

    public String getQueixa() {
        return queixa;
    }

    public StatusEmergencia getStatus() {
        return status;
    }

    public Profissional getMedico() {
        return medico;
    }
}
