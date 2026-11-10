package br.com.hospital.model;

import static br.com.hospital.model.Fixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import br.com.hospital.enums.Especialidade;
import br.com.hospital.enums.SituacaoConsulta;
import br.com.hospital.exception.RegraNegocioException;

class ConsultaTest {

    private ConsultaRotina rotina() {
        return comId(new ConsultaRotina(paciente(), clinico(), DATA_FUTURA, HORA, "Avaliação inicial"));
    }

    // ---- validacao de cada modalidade

    @Test
    void consultaDeRotinaComClinicoGeralEValida() {
        assertDoesNotThrow(() -> rotina().validarCondicoes());
    }

    @Test
    void consultaDeRotinaExigeClinicoGeral() {
        Consulta consulta = new ConsultaRotina(paciente(), cardiologista(), DATA_FUTURA, HORA, "Avaliação");
        RegraNegocioException erro = assertThrows(RegraNegocioException.class, consulta::validarCondicoes);
        assertTrue(erro.getMessage().contains("clínico geral"));
    }

    @Test
    void consultaEspecializadaExigeEspecialistaDaMesmaArea() {
        Consulta compativel = new ConsultaEspecializada(paciente(), cardiologista(), DATA_FUTURA, HORA,
                "Dor no peito", Especialidade.CARDIOLOGIA);
        Consulta incompativel = new ConsultaEspecializada(paciente(), cardiologista(), DATA_FUTURA, HORA,
                "Enxaqueca", Especialidade.NEUROLOGIA);
        Consulta comClinico = new ConsultaEspecializada(paciente(), clinico(), DATA_FUTURA, HORA,
                "Dor no peito", Especialidade.CARDIOLOGIA);

        assertDoesNotThrow(compativel::validarCondicoes);
        assertThrows(RegraNegocioException.class, incompativel::validarCondicoes);
        assertThrows(RegraNegocioException.class, comClinico::validarCondicoes);
    }

    @Test
    void consultaNaoPodeSerAgendadaNoPassado() {
        Consulta consulta = new ConsultaRotina(paciente(), clinico(), LocalDate.now().minusDays(1), HORA, "Retorno");
        assertThrows(RegraNegocioException.class, consulta::validarCondicoes);
    }

    @Test
    void consultaPrecisaDeMotivo() {
        Consulta consulta = new ConsultaRotina(paciente(), clinico(), DATA_FUTURA, HORA, " ");
        assertThrows(RegraNegocioException.class, consulta::validarCondicoes);
    }

    // ---- situacao

    @Test
    void fluxoCompletoAteConclusao() {
        Consulta consulta = rotina();
        consulta.mudarSituacao(SituacaoConsulta.CONFIRMADA);
        consulta.mudarSituacao(SituacaoConsulta.EM_ATENDIMENTO);
        consulta.mudarSituacao(SituacaoConsulta.CONCLUIDA);
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
    }

    @Test
    void consultaConcluidaNaoVoltaParaAgendada() {
        Consulta consulta = rotina();
        consulta.mudarSituacao(SituacaoConsulta.CONFIRMADA);
        consulta.mudarSituacao(SituacaoConsulta.EM_ATENDIMENTO);
        consulta.mudarSituacao(SituacaoConsulta.CONCLUIDA);

        assertThrows(RegraNegocioException.class, () -> consulta.mudarSituacao(SituacaoConsulta.AGENDADA));
        assertEquals(SituacaoConsulta.CONCLUIDA, consulta.getSituacao());
    }

    @Test
    void consultaCanceladaNaoPodeSerConcluida() {
        Consulta consulta = rotina();
        consulta.mudarSituacao(SituacaoConsulta.CANCELADA);
        assertThrows(RegraNegocioException.class, () -> consulta.mudarSituacao(SituacaoConsulta.CONCLUIDA));
    }

    @Test
    void observacoesSoDuranteOuDepoisDoAtendimento() {
        Consulta consulta = rotina();
        assertThrows(RegraNegocioException.class, () -> consulta.registrarObservacoes("Paciente estável"));

        consulta.mudarSituacao(SituacaoConsulta.CONFIRMADA);
        consulta.mudarSituacao(SituacaoConsulta.EM_ATENDIMENTO);
        consulta.registrarObservacoes("Paciente estável");
        assertEquals("Paciente estável", consulta.getObservacoes());
    }

    // ---- remarcacao

    @Test
    void consultaCanceladaSoVoltaAAcontecerComNovoAgendamento() {
        Consulta original = rotina();
        original.mudarSituacao(SituacaoConsulta.CANCELADA);

        Consulta nova = comId(original.remarcar(DATA_FUTURA.plusDays(1), HORA));
        original.confirmarRemarcacao(nova);

        assertNotSame(original, nova);
        assertEquals(SituacaoConsulta.AGENDADA, nova.getSituacao());
        assertEquals(SituacaoConsulta.CANCELADA, original.getSituacao());
        assertEquals(original.getId(), nova.getConsultaOrigemId());
        assertEquals(nova.getId(), original.getRemarcadaParaId());
    }

    @Test
    void remarcarConsultaAtivaCancelaAOriginal() {
        Consulta original = rotina();
        Consulta nova = comId(original.remarcar(DATA_FUTURA.plusDays(1), HORA));
        original.confirmarRemarcacao(nova);
        assertEquals(SituacaoConsulta.CANCELADA, original.getSituacao());
    }

    @Test
    void remarcacaoMantemAModalidadeEAEspecialidade() {
        Consulta original = comId(new ConsultaEspecializada(paciente(), cardiologista(), DATA_FUTURA, HORA,
                "Dor no peito", Especialidade.CARDIOLOGIA));
        Consulta nova = original.remarcar(DATA_FUTURA.plusDays(2), HORA);

        ConsultaEspecializada especializada = assertInstanceOf(ConsultaEspecializada.class, nova);
        assertEquals(Especialidade.CARDIOLOGIA, especializada.getEspecialidade());
        assertSame(original.getMedico(), nova.getMedico());
        assertEquals(DATA_FUTURA.plusDays(2), nova.getData());
    }

    @Test
    void consultaConcluidaNaoPodeSerRemarcada() {
        Consulta consulta = rotina();
        consulta.mudarSituacao(SituacaoConsulta.CONFIRMADA);
        consulta.mudarSituacao(SituacaoConsulta.EM_ATENDIMENTO);
        consulta.mudarSituacao(SituacaoConsulta.CONCLUIDA);
        assertThrows(RegraNegocioException.class, () -> consulta.remarcar(DATA_FUTURA, HORA));
    }

    @Test
    void consultaNaoPodeSerRemarcadaDuasVezes() {
        Consulta original = rotina();
        original.confirmarRemarcacao(comId(original.remarcar(DATA_FUTURA.plusDays(1), HORA)));
        assertThrows(RegraNegocioException.class, () -> original.remarcar(DATA_FUTURA.plusDays(3), HORA));
    }
}
