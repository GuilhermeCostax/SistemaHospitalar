package br.com.hospital.enums;

import static br.com.hospital.enums.SituacaoConsulta.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

class SituacaoConsultaTest {

    @ParameterizedTest(name = "{0} -> {1} e permitido")
    @CsvSource({
            "AGENDADA, CONFIRMADA", "AGENDADA, CANCELADA", "AGENDADA, NAO_COMPARECEU",
            "CONFIRMADA, EM_ATENDIMENTO", "CONFIRMADA, CANCELADA", "CONFIRMADA, NAO_COMPARECEU",
            "EM_ATENDIMENTO, CONCLUIDA", "EM_ATENDIMENTO, CANCELADA"
    })
    void transicoesPermitidas(SituacaoConsulta atual, SituacaoConsulta nova) {
        assertTrue(atual.podeIrPara(nova));
    }

    @ParameterizedTest(name = "{0} -> {1} e bloqueado")
    @CsvSource({
            "AGENDADA, CONCLUIDA", "AGENDADA, EM_ATENDIMENTO",
            "CONFIRMADA, AGENDADA", "EM_ATENDIMENTO, AGENDADA",
            "CONCLUIDA, AGENDADA", "CANCELADA, CONCLUIDA", "CANCELADA, AGENDADA",
            "NAO_COMPARECEU, AGENDADA"
    })
    void transicoesBloqueadas(SituacaoConsulta atual, SituacaoConsulta nova) {
        assertFalse(atual.podeIrPara(nova));
    }

    @ParameterizedTest
    @EnumSource(value = SituacaoConsulta.class, names = {"CONCLUIDA", "CANCELADA", "NAO_COMPARECEU"})
    void situacoesFinaisNaoMudamParaNenhumaOutra(SituacaoConsulta finalizada) {
        for (SituacaoConsulta outra : SituacaoConsulta.values()) {
            assertFalse(finalizada.podeIrPara(outra));
        }
    }

    @Test
    void somenteConsultasQueNaoComecaramOuNaoAconteceramPodemSerRemarcadas() {
        assertTrue(AGENDADA.podeSerRemarcada());
        assertTrue(CONFIRMADA.podeSerRemarcada());
        assertTrue(CANCELADA.podeSerRemarcada());
        assertTrue(NAO_COMPARECEU.podeSerRemarcada());
        assertFalse(EM_ATENDIMENTO.podeSerRemarcada());
        assertFalse(CONCLUIDA.podeSerRemarcada());
    }
}
