package br.com.hospital.enums;

/**
 * Situacoes possiveis de uma consulta. Cada situacao sabe para quais
 * outras ela pode ir, assim nao existe texto livre e nem volta indevida
 * (ex: CONCLUIDA nunca volta para AGENDADA).
 */
public enum SituacaoConsulta {
    AGENDADA("Agendada"),
    CONFIRMADA("Confirmada"),
    EM_ATENDIMENTO("Em atendimento"),
    CONCLUIDA("Concluída"),
    CANCELADA("Cancelada"),
    NAO_COMPARECEU("Não compareceu");

    private final String descricao;

    SituacaoConsulta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeIrPara(SituacaoConsulta nova) {
        switch (this) {
            case AGENDADA:
                return nova == CONFIRMADA || nova == CANCELADA || nova == NAO_COMPARECEU;
            case CONFIRMADA:
                return nova == EM_ATENDIMENTO || nova == CANCELADA || nova == NAO_COMPARECEU;
            case EM_ATENDIMENTO:
                return nova == CONCLUIDA || nova == CANCELADA;
            default:
                // CONCLUIDA, CANCELADA e NAO_COMPARECEU sao finais
                return false;
        }
    }

    public boolean estaAtiva() {
        return this == AGENDADA || this == CONFIRMADA || this == EM_ATENDIMENTO;
    }

    /**
     * Uma consulta que ainda nao comecou, foi cancelada ou teve falta pode ser
     * remarcada. A remarcacao gera um novo agendamento; a consulta antiga nunca
     * "volta" para AGENDADA.
     */
    public boolean podeSerRemarcada() {
        return this == AGENDADA || this == CONFIRMADA || this == CANCELADA || this == NAO_COMPARECEU;
    }
}
