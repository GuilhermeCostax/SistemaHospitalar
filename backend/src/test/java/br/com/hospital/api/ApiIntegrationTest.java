package br.com.hospital.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Testa as rotas da API de ponta a ponta. Os repositorios ficam em memoria,
 * entao o contexto e recriado a cada teste para comecar sempre do zero.
 * Ids criados no setUp: paciente 1, clinico 1, cardiologista 2, cirurgiao 3.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ApiIntegrationTest {

    private static final String DATA = LocalDate.now().plusDays(10).toString();

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setUp() throws Exception {
        enviar(post("/pacientes"), """
                {"nome": "Ana", "cpf": "111", "dataNascimento": "1990-01-01"}""").andExpect(status().isCreated());
        enviar(post("/profissionais"), """
                {"nome": "Rafael", "registro": "CRM-1", "categoria": "CLINICO_GERAL"}""").andExpect(status().isCreated());
        enviar(post("/profissionais"), """
                {"nome": "Helena", "registro": "CRM-2", "categoria": "ESPECIALISTA",
                 "especialidade": "CARDIOLOGIA"}""").andExpect(status().isCreated());
        enviar(post("/profissionais"), """
                {"nome": "Paulo", "registro": "CRM-3", "categoria": "CIRURGIAO",
                 "areaCirurgica": "Geral"}""").andExpect(status().isCreated());
    }

    private ResultActions enviar(MockHttpServletRequestBuilder requisicao, String json) throws Exception {
        return mvc.perform(requisicao.contentType(MediaType.APPLICATION_JSON).content(json));
    }

    private ResultActions agendarRotina(String hora) throws Exception {
        return enviar(post("/consultas"), """
                {"tipo": "ROTINA", "pacienteId": 1, "medicoId": 1, "data": "%s", "hora": "%s",
                 "motivo": "Avaliação"}""".formatted(DATA, hora));
    }

    // ---------- profissionais

    @Test
    void cadaCategoriaApareceComSeusDados() throws Exception {
        mvc.perform(get("/profissionais"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].categoria").value("CLINICO_GERAL"))
                .andExpect(jsonPath("$[1].especialidade").value("CARDIOLOGIA"))
                .andExpect(jsonPath("$[2].areaCirurgica").value("Geral"));
    }

    @Test
    void especialistaSemEspecialidadeERecusado() throws Exception {
        enviar(post("/profissionais"), """
                {"nome": "X", "registro": "CRM-9", "categoria": "ESPECIALISTA"}""")
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listaProfissionaisPorAreaEDisponibilidade() throws Exception {
        mvc.perform(get("/profissionais").param("especialidade", "CARDIOLOGIA"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nome").value("Helena"));

        agendarRotina("09:00").andExpect(status().isCreated());
        mvc.perform(get("/profissionais/disponiveis").param("categoria", "CLINICO_GERAL")
                        .param("data", DATA).param("hora", "09:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/profissionais/disponiveis").param("categoria", "CLINICO_GERAL")
                        .param("data", DATA).param("hora", "10:00"))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void categoriaNaoPodeSerAlteradaNaEdicao() throws Exception {
        enviar(put("/profissionais/1"), """
                {"nome": "Rafael", "registro": "CRM-1", "categoria": "CIRURGIAO", "areaCirurgica": "Geral"}""")
                .andExpect(status().isUnprocessableEntity());
        enviar(put("/profissionais/1"), """
                {"nome": "Rafael Mendes", "registro": "CRM-1", "categoria": "CLINICO_GERAL"}""")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Rafael Mendes"));
    }

    // ---------- consultas

    @Test
    void consultaDeRotinaComEspecialistaERecusada() throws Exception {
        enviar(post("/consultas"), """
                {"tipo": "ROTINA", "pacienteId": 1, "medicoId": 2, "data": "%s", "hora": "09:00",
                 "motivo": "Avaliação"}""".formatted(DATA))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erro").value("Consulta de rotina deve ser realizada por um clínico geral."));
    }

    @Test
    void consultaEspecializadaExigeAreaCompativel() throws Exception {
        enviar(post("/consultas"), """
                {"tipo": "ESPECIALIZADA", "pacienteId": 1, "medicoId": 2, "data": "%s", "hora": "09:00",
                 "motivo": "Enxaqueca", "especialidade": "NEUROLOGIA"}""".formatted(DATA))
                .andExpect(status().isUnprocessableEntity());
        enviar(post("/consultas"), """
                {"tipo": "ESPECIALIZADA", "pacienteId": 1, "medicoId": 2, "data": "%s", "hora": "09:00",
                 "motivo": "Dor no peito", "especialidade": "CARDIOLOGIA"}""".formatted(DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modalidade").value("CONSULTA_ESPECIALIZADA"));
    }

    @Test
    void medicoNaoPodeTerDuasConsultasNoMesmoHorario() throws Exception {
        agendarRotina("09:00").andExpect(status().isCreated());
        agendarRotina("09:00").andExpect(status().isUnprocessableEntity());
    }

    @Test
    void situacaoSegueOFluxoEBloqueiaRetrocesso() throws Exception {
        agendarRotina("09:00").andExpect(jsonPath("$.situacao").value("AGENDADA"));

        enviar(put("/consultas/1/situacao"), "{\"situacao\": \"CONCLUIDA\"}")
                .andExpect(status().isUnprocessableEntity());
        for (String s : new String[] {"CONFIRMADA", "EM_ATENDIMENTO", "CONCLUIDA"}) {
            enviar(put("/consultas/1/situacao"), "{\"situacao\": \"" + s + "\"}").andExpect(status().isOk());
        }
        enviar(put("/consultas/1/situacao"), "{\"situacao\": \"AGENDADA\"}")
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(get("/consultas").param("situacao", "CONCLUIDA")).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void situacaoForaDoEnumERecusada() throws Exception {
        agendarRotina("09:00");
        enviar(put("/consultas/1/situacao"), "{\"situacao\": \"FINALIZADA\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultaCanceladaERemarcadaComNovoAgendamento() throws Exception {
        agendarRotina("09:00");
        enviar(put("/consultas/1/situacao"), "{\"situacao\": \"CANCELADA\"}").andExpect(status().isOk());

        enviar(post("/consultas/1/remarcar"), """
                {"data": "%s", "hora": "11:00"}""".formatted(DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.situacao").value("AGENDADA"))
                .andExpect(jsonPath("$.consultaOrigemId").value(1));

        mvc.perform(get("/consultas/1"))
                .andExpect(jsonPath("$.situacao").value("CANCELADA"))
                .andExpect(jsonPath("$.remarcadaParaId").value(2));
        enviar(post("/consultas/1/remarcar"), """
                {"data": "%s", "hora": "12:00"}""".formatted(DATA))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void clinicoGeralEncaminhaParaEspecialista() throws Exception {
        agendarRotina("09:00");
        enviar(put("/consultas/1/situacao"), "{\"situacao\": \"CONFIRMADA\"}");
        enviar(put("/consultas/1/situacao"), "{\"situacao\": \"EM_ATENDIMENTO\"}");

        enviar(post("/consultas/1/encaminhamento"), """
                {"especialidade": "CARDIOLOGIA", "medicoId": 2, "data": "%s", "hora": "14:00"}""".formatted(DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.modalidade").value("CONSULTA_ESPECIALIZADA"))
                .andExpect(jsonPath("$.medico.id").value(2));
    }

    // ---------- emergencia e cirurgia

    @Test
    void filaDeEmergenciaOrdenadaPorPrioridade() throws Exception {
        enviar(post("/emergencias"), "{\"pacienteId\": 1, \"prioridade\": \"BAIXA\", \"queixa\": \"Corte\"}");
        enviar(post("/emergencias"), "{\"pacienteId\": 1, \"prioridade\": \"CRITICA\", \"queixa\": \"Parada\"}");
        enviar(put("/emergencias/1/iniciar"), "{\"medicoId\": 1}").andExpect(status().isOk());

        mvc.perform(get("/emergencias").param("status", "AGUARDANDO_AVALIACAO"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].prioridade").value("CRITICA"));
        mvc.perform(get("/emergencias"))
                .andExpect(jsonPath("$[0].prioridade").value("CRITICA"))
                .andExpect(jsonPath("$[1].status").value("EM_ATENDIMENTO"));
    }

    @Test
    void cirurgiaNaoConcluiSemIniciarEApareceNoCirurgiao() throws Exception {
        enviar(post("/cirurgias"), """
                {"pacienteId": 1, "cirurgiaoId": 3, "dataPrevista": "%s", "horaPrevista": "08:00",
                 "descricao": "Apendicectomia", "tipo": "ELETIVA"}""".formatted(DATA))
                .andExpect(status().isCreated());
        enviar(post("/cirurgias"), """
                {"pacienteId": 1, "cirurgiaoId": 1, "dataPrevista": "%s", "horaPrevista": "08:00",
                 "descricao": "Biópsia", "tipo": "ELETIVA"}""".formatted(DATA))
                .andExpect(status().isUnprocessableEntity());

        mvc.perform(put("/cirurgias/1/concluir")).andExpect(status().isUnprocessableEntity());
        mvc.perform(put("/cirurgias/1/iniciar")).andExpect(status().isOk());
        mvc.perform(put("/cirurgias/1/concluir"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDO"));
        mvc.perform(get("/profissionais/3/procedimentos"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].descricao").value("Apendicectomia"));
    }

    // ---------- quartos e internacoes

    @Test
    void quartoRespeitaCapacidadeDoTipoEOcupacao() throws Exception {
        enviar(post("/quartos"), "{\"numero\": \"301\", \"tipo\": \"UTI\", \"capacidade\": 2}")
                .andExpect(status().isUnprocessableEntity());
        enviar(post("/quartos"), "{\"numero\": \"301\", \"tipo\": \"UTI\", \"capacidade\": 1}")
                .andExpect(status().isCreated());
        enviar(post("/pacientes"), "{\"nome\": \"Beto\", \"cpf\": \"222\", \"dataNascimento\": \"1980-01-01\"}");

        enviar(post("/internacoes"), "{\"pacienteId\": 1, \"medicoId\": 1, \"quartoId\": 1}")
                .andExpect(status().isCreated());
        enviar(post("/internacoes"), "{\"pacienteId\": 2, \"medicoId\": 1, \"quartoId\": 1}")
                .andExpect(status().isUnprocessableEntity());
        mvc.perform(get("/quartos").param("disponiveis", "true")).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(delete("/quartos/1")).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void trocaDeQuartoEAlta() throws Exception {
        enviar(post("/quartos"), "{\"numero\": \"201\", \"tipo\": \"APARTAMENTO\", \"capacidade\": 1}");
        enviar(post("/quartos"), "{\"numero\": \"202\", \"tipo\": \"ENFERMARIA\", \"capacidade\": 2}");
        enviar(post("/internacoes"), "{\"pacienteId\": 1, \"medicoId\": 1, \"quartoId\": 1}");

        enviar(put("/internacoes/1/trocar-quarto"), "{\"quartoId\": 2}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quarto.numero").value("202"));
        mvc.perform(get("/quartos/1")).andExpect(jsonPath("$.ocupacao").value(0));

        enviar(put("/internacoes/1/alta"), "{}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ALTA_REALIZADA"));
        mvc.perform(get("/quartos/2")).andExpect(jsonPath("$.ocupacao").value(0));
        enviar(put("/internacoes/1/trocar-quarto"), "{\"quartoId\": 1}")
                .andExpect(status().isUnprocessableEntity());
    }

    // ---------- pacientes

    @Test
    void pacienteComHistoricoNaoEExcluido() throws Exception {
        enviar(post("/pacientes"), "{\"nome\": \"Caio\", \"cpf\": \"333\", \"dataNascimento\": \"2000-01-01\"}");
        agendarRotina("09:00");

        mvc.perform(delete("/pacientes/1")).andExpect(status().isUnprocessableEntity());
        mvc.perform(delete("/pacientes/2")).andExpect(status().isNoContent());
        mvc.perform(get("/pacientes/2")).andExpect(status().isNotFound());
    }

    @Test
    void cpfRepetidoERecusado() throws Exception {
        enviar(post("/pacientes"), "{\"nome\": \"Outra\", \"cpf\": \"111\", \"dataNascimento\": \"1990-01-01\"}")
                .andExpect(status().isUnprocessableEntity());
    }
}
