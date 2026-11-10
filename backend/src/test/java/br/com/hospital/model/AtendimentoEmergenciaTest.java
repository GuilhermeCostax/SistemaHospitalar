package br.com.hospital.model;

import static br.com.hospital.model.Fixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import br.com.hospital.enums.PrioridadeEmergencia;
import br.com.hospital.enums.StatusEmergencia;
import br.com.hospital.exception.RegraNegocioException;

class AtendimentoEmergenciaTest {

    @Test
    void comecaAguardandoAvaliacaoESemMedico() {
        AtendimentoEmergencia e = new AtendimentoEmergencia(paciente(), PrioridadeEmergencia.ALTA, "Falta de ar");
        assertDoesNotThrow(e::validarCondicoes);
        assertEquals(StatusEmergencia.AGUARDANDO_AVALIACAO, e.getStatus());
        assertNull(e.getMedico());
    }

    @Test
    void exigePrioridadeEQueixa() {
        assertThrows(RegraNegocioException.class,
                () -> new AtendimentoEmergencia(paciente(), null, "Dor").validarCondicoes());
        assertThrows(RegraNegocioException.class,
                () -> new AtendimentoEmergencia(paciente(), PrioridadeEmergencia.BAIXA, "").validarCondicoes());
    }

    @Test
    void fluxoAguardandoEmAtendimentoConcluido() {
        AtendimentoEmergencia e = new AtendimentoEmergencia(paciente(), PrioridadeEmergencia.CRITICA, "Parada");
        assertThrows(RegraNegocioException.class, e::concluir);

        ClinicoGeral medico = clinico();
        e.iniciar(medico);
        assertEquals(StatusEmergencia.EM_ATENDIMENTO, e.getStatus());
        assertSame(medico, e.getMedico());
        assertThrows(RegraNegocioException.class, () -> e.iniciar(medico));

        e.concluir();
        assertEquals(StatusEmergencia.CONCLUIDO, e.getStatus());
    }

    @Test
    void prioridadesTemNiveisCrescentes() {
        assertTrue(PrioridadeEmergencia.CRITICA.getNivel() > PrioridadeEmergencia.ALTA.getNivel());
        assertTrue(PrioridadeEmergencia.ALTA.getNivel() > PrioridadeEmergencia.MODERADA.getNivel());
        assertTrue(PrioridadeEmergencia.MODERADA.getNivel() > PrioridadeEmergencia.BAIXA.getNivel());
    }
}
