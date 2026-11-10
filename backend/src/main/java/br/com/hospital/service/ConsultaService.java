package br.com.hospital.service;

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

    // aplica a validacao da modalidade e verifica se o medico esta livre no horario
    private Consulta salvarSeValida(Consulta consulta) {
        consulta.validarCondicoes();

        boolean horarioOcupado = repository.listar().stream()
                .filter(c -> c.getSituacao().estaAtiva())
                .anyMatch(c -> c.getMedico().getId().equals(consulta.getMedico().getId())
                        && c.getData().equals(consulta.getData())
                        && c.getHora().equals(consulta.getHora()));
        if (horarioOcupado) {
            throw new RegraNegocioException("O médico já possui uma consulta neste dia e horário.");
        }
        return repository.salvar(consulta);
    }
}
