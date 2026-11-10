package br.com.hospital.model;

import static br.com.hospital.model.Fixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import br.com.hospital.enums.StatusCirurgia;
import br.com.hospital.enums.TipoCirurgia;
import br.com.hospital.exception.RegraNegocioException;

class ProcedimentoCirurgicoTest {

    private ProcedimentoCirurgico procedimento(Cirurgiao cirurgiao) {
        return new ProcedimentoCirurgico(paciente(), cirurgiao, DATA_FUTURA, HORA,
                "Apendicectomia", TipoCirurgia.ELETIVA, "Jejum de 8 horas");
    }

    @Test
    void procedimentoValidoComecaAgendado() {
        ProcedimentoCirurgico p = procedimento(cirurgiao());
        assertDoesNotThrow(p::validarCondicoes);
        assertEquals(StatusCirurgia.AGENDADO, p.getStatus());
        assertNull(p.getDataEfetiva());
    }

    @Test
    void exigeCirurgiaoDescricaoETipo() {
        assertThrows(RegraNegocioException.class, () -> procedimento(null).validarCondicoes());
        assertThrows(RegraNegocioException.class, () -> new ProcedimentoCirurgico(paciente(), cirurgiao(),
                DATA_FUTURA, HORA, "", TipoCirurgia.ELETIVA, null).validarCondicoes());
        assertThrows(RegraNegocioException.class, () -> new ProcedimentoCirurgico(paciente(), cirurgiao(),
                DATA_FUTURA, HORA, "Biópsia", null, null).validarCondicoes());
    }

    @Test
    void naoPodeSerAgendadoNoPassado() {
        ProcedimentoCirurgico p = new ProcedimentoCirurgico(paciente(), cirurgiao(), LocalDate.now().minusDays(1),
                HORA, "Biópsia", TipoCirurgia.ELETIVA, null);
        assertThrows(RegraNegocioException.class, p::validarCondicoes);
    }

    @Test
    void naoPodeSerConcluidoSemTerSidoIniciado() {
        ProcedimentoCirurgico p = procedimento(cirurgiao());
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, p::concluir);
        assertTrue(erro.getMessage().contains("não foi iniciado"));
        assertEquals(StatusCirurgia.AGENDADO, p.getStatus());
    }

    @Test
    void iniciarRegistraDataEHoraEfetivas() {
        ProcedimentoCirurgico p = procedimento(cirurgiao());
        p.iniciar();
        assertEquals(StatusCirurgia.EM_ANDAMENTO, p.getStatus());
        assertEquals(LocalDate.now(), p.getDataEfetiva());
        assertNotNull(p.getHoraEfetiva());

        p.concluir();
        assertEquals(StatusCirurgia.CONCLUIDO, p.getStatus());
    }

    @Test
    void soCancelaProcedimentoAgendado() {
        ProcedimentoCirurgico p = procedimento(cirurgiao());
        p.iniciar();
        assertThrows(RegraNegocioException.class, p::cancelar);

        ProcedimentoCirurgico outro = procedimento(cirurgiao());
        outro.cancelar();
        assertEquals(StatusCirurgia.CANCELADO, outro.getStatus());
        assertThrows(RegraNegocioException.class, outro::iniciar);
    }

    @Test
    void posOperatorioSoDepoisDeIniciado() {
        ProcedimentoCirurgico p = procedimento(cirurgiao());
        assertThrows(RegraNegocioException.class, () -> p.registrarPosOperatorio("Sem intercorrências"));
        p.iniciar();
        p.registrarPosOperatorio("Sem intercorrências");
        assertEquals("Sem intercorrências", p.getObservacoesPosOperatorias());
    }

    @Test
    void cirurgiaoSabeQuandoEstaOcupado() {
        Cirurgiao cirurgiao = cirurgiao();
        ProcedimentoCirurgico p = procedimento(cirurgiao);
        cirurgiao.registrarProcedimento(p);

        assertTrue(cirurgiao.temProcedimentoEm(DATA_FUTURA, HORA));
        assertTrue(cirurgiao.temProcedimentosPendentes());

        p.cancelar();
        assertFalse(cirurgiao.temProcedimentoEm(DATA_FUTURA, HORA));
        assertFalse(cirurgiao.temProcedimentosPendentes());
    }
}
