package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.hospital.enums.SituacaoConsulta;
import br.com.hospital.exception.RegraNegocioException;

public abstract class Consulta extends Atendimento {

    protected Profissional medico;
    protected String motivo;
    protected SituacaoConsulta situacao = SituacaoConsulta.AGENDADA;

    public Consulta(Paciente paciente, Profissional medico, LocalDate data, LocalTime hora, String motivo) {
        super(paciente, data, hora);
        this.medico = medico;
        this.motivo = motivo;
    }

    @Override
    public void validarCondicoes() {
        validarCamposComuns();
        if (medico == null) {
            throw new RegraNegocioException("A consulta precisa de um médico responsável.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new RegraNegocioException("A consulta precisa de um motivo.");
        }
        if (LocalDateTime.of(data, hora).isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível agendar uma consulta no passado.");
        }
    }

    public void mudarSituacao(SituacaoConsulta nova) {
        if (!situacao.podeIrPara(nova)) {
            throw new RegraNegocioException("Não é possível mudar a consulta de '"
                    + situacao.getDescricao() + "' para '" + nova.getDescricao() + "'.");
        }
        situacao = nova;
    }

    // observacoes so podem ser registradas durante ou depois do atendimento
    public void registrarObservacoes(String texto) {
        if (situacao != SituacaoConsulta.EM_ATENDIMENTO && situacao != SituacaoConsulta.CONCLUIDA) {
            throw new RegraNegocioException("Observações só podem ser registradas em consultas em atendimento ou concluídas.");
        }
        this.observacoes = texto;
    }

    public Profissional getMedico() {
        return medico;
    }

    public String getMotivo() {
        return motivo;
    }

    public SituacaoConsulta getSituacao() {
        return situacao;
    }
}
