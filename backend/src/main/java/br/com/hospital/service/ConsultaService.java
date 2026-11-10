package br.com.hospital.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.ConsultaRequest;
import br.com.hospital.dto.EncaminhamentoRequest;
import br.com.hospital.enums.SituacaoConsulta;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.ClinicoGeral;
import br.com.hospital.model.Consulta;
import br.com.hospital.model.ConsultaEspecializada;
import br.com.hospital.model.ConsultaRotina;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.Profissional;
import br.com.hospital.repository.ConsultaRepository;

@Service
public class ConsultaService {

    private final ConsultaRepository repository;
    private final PacienteService pacienteService;
    private final ProfissionalService profissionalService;

    public ConsultaService(ConsultaRepository repository, PacienteService pacienteService,
                           ProfissionalService profissionalService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
        this.profissionalService = profissionalService;
    }

    public List<Consulta> listar(SituacaoConsulta situacao) {
        if (situacao == null) {
            return repository.listar();
        }
        return repository.listar().stream().filter(c -> c.getSituacao() == situacao).toList();
    }

    public Consulta buscar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Consulta " + id + " não encontrada."));
    }

    public Consulta agendar(ConsultaRequest dados) {
        Paciente paciente = pacienteService.buscar(dados.pacienteId());
        Profissional medico = profissionalService.buscar(dados.medicoId());

        Consulta consulta;
        if (dados.tipo() == br.com.hospital.enums.TipoConsulta.ROTINA) {
            consulta = new ConsultaRotina(paciente, medico, dados.data(), dados.hora(), dados.motivo());
        } else {
            consulta = new ConsultaEspecializada(paciente, medico, dados.data(), dados.hora(),
                    dados.motivo(), dados.especialidade());
        }
        return salvarSeValida(consulta);
    }

    public Consulta mudarSituacao(Long id, SituacaoConsulta nova) {
        Consulta consulta = buscar(id);
        consulta.mudarSituacao(nova);
        return repository.salvar(consulta);
    }

    public Consulta registrarObservacoes(Long id, String observacoes) {
        Consulta consulta = buscar(id);
        consulta.registrarObservacoes(observacoes);
        return repository.salvar(consulta);
    }

    /** O clinico geral encaminha o paciente para um especialista, gerando uma consulta especializada. */
    public Consulta encaminhar(Long consultaId, EncaminhamentoRequest dados) {
        Consulta origem = buscar(consultaId);
        if (!(origem.getMedico() instanceof ClinicoGeral)) {
            throw new RegraNegocioException("Somente consultas de um clínico geral podem gerar encaminhamento.");
        }
        if (origem.getSituacao() != SituacaoConsulta.EM_ATENDIMENTO
                && origem.getSituacao() != SituacaoConsulta.CONCLUIDA) {
            throw new RegraNegocioException("O encaminhamento só pode ser feito durante ou depois da consulta.");
        }
        Profissional especialista = profissionalService.buscar(dados.medicoId());
        String motivo = (dados.motivo() == null || dados.motivo().isBlank())
                ? "Encaminhamento da consulta " + origem.getId()
                : dados.motivo();

        Consulta nova = new ConsultaEspecializada(origem.getPaciente(), especialista, dados.data(),
                dados.hora(), motivo, dados.especialidade());
        return salvarSeValida(nova);
    }

    /**
     * Remarca a consulta criando um novo agendamento. A original fica cancelada
     * (se ainda estava ativa) e as duas ficam ligadas pelos ids.
     */
    public Consulta remarcar(Long id, LocalDate novaData, LocalTime novaHora) {
        Consulta origem = buscar(id);
        Consulta nova = origem.remarcar(novaData, novaHora);
        salvarSeValida(nova);
        origem.confirmarRemarcacao(nova);
        repository.salvar(origem);
        return nova;
    }

    /** Verdadeiro se o medico ja tem uma consulta ativa neste dia e horario. */
    public boolean medicoTemConsulta(Long medicoId, LocalDate data, LocalTime hora) {
        return repository.listar().stream()
                .filter(c -> c.getSituacao().estaAtiva())
                .anyMatch(c -> c.getMedico().getId().equals(medicoId)
                        && c.getData().equals(data)
                        && c.getHora().equals(hora));
    }

    // aplica a validacao da modalidade e verifica se o medico esta livre no horario
    private Consulta salvarSeValida(Consulta consulta) {
        consulta.validarCondicoes();

        if (medicoTemConsulta(consulta.getMedico().getId(), consulta.getData(), consulta.getHora())) {
            throw new RegraNegocioException("O médico já possui uma consulta neste dia e horário.");
        }
        return repository.salvar(consulta);
    }
}
