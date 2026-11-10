package br.com.hospital.model;

import static br.com.hospital.model.Fixtures.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import br.com.hospital.enums.StatusInternacao;
import br.com.hospital.enums.TipoQuarto;
import br.com.hospital.exception.RegraNegocioException;

class QuartoEInternacaoTest {

    private static final LocalDate HOJE = LocalDate.now();

    private Internacao internar(Quarto quarto) {
        quarto.ocupar();
        return new Internacao(paciente(), clinico(), quarto, HOJE, HOJE.plusDays(3), null);
    }

    // ---- tipos de quarto

    @Test
    void capacidadeRespeitaOMaximoDoTipo() {
        assertDoesNotThrow(() -> quarto(TipoQuarto.ENFERMARIA, 4));
        assertThrows(RegraNegocioException.class, () -> quarto(TipoQuarto.APARTAMENTO, 2));
        assertThrows(RegraNegocioException.class, () -> quarto(TipoQuarto.UTI, 2));
        assertThrows(RegraNegocioException.class, () -> quarto(TipoQuarto.ENFERMARIA, 0));
    }

    @Test
    void quartoLotadoNaoAceitaMaisPacientes() {
        Quarto quarto = quarto(TipoQuarto.ENFERMARIA, 2);
        quarto.ocupar();
        quarto.ocupar();
        assertFalse(quarto.temVaga());
        assertThrows(RegraNegocioException.class, quarto::ocupar);
        assertEquals(2, quarto.getOcupacao());
    }

    @Test
    void capacidadeNaoPodeFicarMenorQueOcupacao() {
        Quarto quarto = quarto(TipoQuarto.ENFERMARIA, 3);
        quarto.ocupar();
        quarto.ocupar();
        assertThrows(RegraNegocioException.class,
                () -> quarto.atualizar(quarto.getNumero(), "1º andar", TipoQuarto.ENFERMARIA, 1));
        quarto.atualizar(quarto.getNumero(), "1º andar", TipoQuarto.ENFERMARIA, 2);
        assertEquals(2, quarto.getCapacidade());
    }

    @Test
    void valorDaDiariaVemDoTipo() {
        assertEquals(TipoQuarto.UTI.getValorDiaria(), quarto(TipoQuarto.UTI, 1).getValorDiaria());
    }

    // ---- internacao

    @Test
    void previsaoDeAltaNaoPodeSerAnteriorAEntrada() {
        Internacao internacao = new Internacao(paciente(), clinico(), quarto(TipoQuarto.UTI, 1),
                HOJE, HOJE.minusDays(1), null);
        assertThrows(RegraNegocioException.class, internacao::validar);
    }

    @Test
    void altaLiberaOQuartoESoAconteceUmaVez() {
        Quarto quarto = quarto(TipoQuarto.APARTAMENTO, 1);
        Internacao internacao = internar(quarto);
        assertFalse(quarto.temVaga());

        internacao.darAlta(HOJE.plusDays(1));
        assertEquals(StatusInternacao.ALTA_REALIZADA, internacao.getStatus());
        assertTrue(quarto.temVaga());
        assertThrows(RegraNegocioException.class, () -> internacao.darAlta(HOJE.plusDays(2)));
    }

    @Test
    void altaNaoPodeSerAntesDaEntrada() {
        Internacao internacao = internar(quarto(TipoQuarto.UTI, 1));
        assertThrows(RegraNegocioException.class, () -> internacao.darAlta(HOJE.minusDays(1)));
    }

    @Test
    void trocaDeQuartoOcupaONovoELiberaOAntigo() {
        Quarto antigo = quarto(TipoQuarto.APARTAMENTO, 1);
        Quarto novo = quarto(TipoQuarto.ENFERMARIA, 2);
        Internacao internacao = internar(antigo);

        internacao.trocarQuarto(novo);

        assertSame(novo, internacao.getQuarto());
        assertEquals(0, antigo.getOcupacao());
        assertEquals(1, novo.getOcupacao());
    }

    @Test
    void trocaParaQuartoLotadoMantemOQuartoAtual() {
        Quarto antigo = quarto(TipoQuarto.APARTAMENTO, 1);
        Quarto lotado = quarto(TipoQuarto.UTI, 1);
        lotado.ocupar();
        Internacao internacao = internar(antigo);

        assertThrows(RegraNegocioException.class, () -> internacao.trocarQuarto(lotado));
        assertSame(antigo, internacao.getQuarto());
        assertEquals(1, antigo.getOcupacao());
    }

    @Test
    void naoTrocaQuartoParaOMesmoNemDepoisDaAlta() {
        Quarto quarto = quarto(TipoQuarto.ENFERMARIA, 2);
        Internacao internacao = internar(quarto);
        assertThrows(RegraNegocioException.class, () -> internacao.trocarQuarto(quarto));

        internacao.darAlta(HOJE);
        assertThrows(RegraNegocioException.class,
                () -> internacao.trocarQuarto(quarto(TipoQuarto.UTI, 1)));
        assertThrows(RegraNegocioException.class, () -> internacao.atualizar(HOJE.plusDays(5), "x"));
    }
}
