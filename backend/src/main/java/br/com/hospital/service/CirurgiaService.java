package br.com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.CirurgiaRequest;
import br.com.hospital.enums.StatusCirurgia;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.repository.CirurgiaRepository;

@Service
public class CirurgiaService {

    private final CirurgiaRepository repository;
    private final PacienteService pacienteService;
    private final ProfissionalService profissionalService;

    public CirurgiaService(CirurgiaRepository repository, PacienteService pacienteService,
                           ProfissionalService profissionalService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
        this.profissionalService = profissionalService;
    }

    public List<ProcedimentoCirurgico> listar(StatusCirurgia status) {
        return repository.listar().stream()
                .filter(c -> status == null || c.getStatus() == status)
                .toList();
    }

    public ProcedimentoCirurgico buscar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Procedimento cirúrgico " + id + " não encontrado."));
    }

    public ProcedimentoCirurgico agendar(CirurgiaRequest dados) {
        Paciente paciente = pacienteService.buscar(dados.pacienteId());
        Cirurgiao cirurgiao = profissionalService.buscarCirurgiao(dados.cirurgiaoId());

        ProcedimentoCirurgico procedimento = new ProcedimentoCirurgico(paciente, cirurgiao,
                dados.dataPrevista(), dados.horaPrevista(), dados.descricao(), dados.tipo(),
                dados.observacoesPreOperatorias());
        procedimento.validarCondicoes();

        // o cirurgiao nao pode estar em duas cirurgias no mesmo horario
        boolean ocupado = cirurgiao.getProcedimentos().stream()
                .filter(p -> p.getStatus() == StatusCirurgia.AGENDADO || p.getStatus() == StatusCirurgia.EM_ANDAMENTO)
                .anyMatch(p -> p.getData().equals(dados.dataPrevista()) && p.getHora().equals(dados.horaPrevista()));
        if (ocupado) {
            throw new RegraNegocioException("O cirurgião já possui um procedimento neste dia e horário.");
        }

        repository.salvar(procedimento);
        cirurgiao.registrarProcedimento(procedimento);
        return procedimento;
    }

    public ProcedimentoCirurgico iniciar(Long id) {
        ProcedimentoCirurgico procedimento = buscar(id);
        procedimento.iniciar();
        return procedimento;
    }

    public ProcedimentoCirurgico concluir(Long id) {
        ProcedimentoCirurgico procedimento = buscar(id);
        procedimento.concluir();
        return procedimento;
    }

    public ProcedimentoCirurgico cancelar(Long id) {
        ProcedimentoCirurgico procedimento = buscar(id);
        procedimento.cancelar();
        return procedimento;
    }

    public ProcedimentoCirurgico registrarPosOperatorio(Long id, String texto) {
        ProcedimentoCirurgico procedimento = buscar(id);
        procedimento.registrarPosOperatorio(texto);
        return procedimento;
    }
}
