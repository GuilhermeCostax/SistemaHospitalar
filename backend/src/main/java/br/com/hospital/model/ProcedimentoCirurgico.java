package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import br.com.hospital.enums.StatusCirurgia;
import br.com.hospital.enums.TipoCirurgia;
import br.com.hospital.exception.RegraNegocioException;

public class ProcedimentoCirurgico extends Atendimento {

    private Cirurgiao cirurgiao;
    private String descricao;
    private TipoCirurgia tipo;
    private String observacoesPreOperatorias;
    private String observacoesPosOperatorias;
    private StatusCirurgia status = StatusCirurgia.AGENDADO;
    // data e hora em que realmente aconteceu (a "data" e "hora" da classe base sao as previstas)
    private LocalDate dataEfetiva;
    private LocalTime horaEfetiva;

    public ProcedimentoCirurgico(Paciente paciente, Cirurgiao cirurgiao, LocalDate dataPrevista, LocalTime horaPrevista,
                                 String descricao, TipoCirurgia tipo, String observacoesPreOperatorias) {
        super(paciente, dataPrevista, horaPrevista);
        this.cirurgiao = cirurgiao;
        this.descricao = descricao;
        this.tipo = tipo;
        this.observacoesPreOperatorias = observacoesPreOperatorias;
    }

    @Override
    public void validarCondicoes() {
        validarCamposComuns();
        if (cirurgiao == null) {
            throw new RegraNegocioException("O procedimento precisa de um cirurgião responsável.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new RegraNegocioException("O procedimento precisa de uma descrição.");
        }
        if (tipo == null) {
            throw new RegraNegocioException("Informe o tipo da cirurgia.");
        }
        if (LocalDateTime.of(data, hora).isBefore(LocalDateTime.now())) {
            throw new RegraNegocioException("Não é possível agendar um procedimento no passado.");
        }
    }

    public void iniciar() {
        if (status != StatusCirurgia.AGENDADO) {
            throw new RegraNegocioException("Só é possível iniciar um procedimento agendado.");
        }
        status = StatusCirurgia.EM_ANDAMENTO;
        dataEfetiva = LocalDate.now();
        horaEfetiva = LocalTime.now().withNano(0);
    }

    public void concluir() {
        if (status != StatusCirurgia.EM_ANDAMENTO) {
            throw new RegraNegocioException("Não é possível concluir um procedimento que não foi iniciado.");
        }
        status = StatusCirurgia.CONCLUIDO;
    }

    public void cancelar() {
        if (status != StatusCirurgia.AGENDADO) {
            throw new RegraNegocioException("Só é possível cancelar um procedimento que ainda está agendado.");
        }
        status = StatusCirurgia.CANCELADO;
    }

    public void registrarPosOperatorio(String texto) {
        if (status != StatusCirurgia.EM_ANDAMENTO && status != StatusCirurgia.CONCLUIDO) {
            throw new RegraNegocioException("Observações pós-operatórias só podem ser registradas depois que o procedimento começa.");
        }
        this.observacoesPosOperatorias = texto;
        this.observacoes = texto;
    }

    /** Agendado ou em andamento: ainda ocupa a agenda do cirurgiao. */
    public boolean estaPendente() {
        return status == StatusCirurgia.AGENDADO || status == StatusCirurgia.EM_ANDAMENTO;
    }

    @Override
    public String getModalidade() {
        return "PROCEDIMENTO_CIRURGICO";
    }

    public Cirurgiao getCirurgiao() {
        return cirurgiao;
    }

    public String getDescricao() {
        return descricao;
    }

    public TipoCirurgia getTipo() {
        return tipo;
    }

    public String getObservacoesPreOperatorias() {
        return observacoesPreOperatorias;
    }

    public String getObservacoesPosOperatorias() {
        return observacoesPosOperatorias;
    }

    public StatusCirurgia getStatus() {
        return status;
    }

    public LocalDate getDataEfetiva() {
        return dataEfetiva;
    }

    public LocalTime getHoraEfetiva() {
        return horaEfetiva;
    }
}
