package br.com.hospital.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.AtualizacaoInternacaoRequest;
import br.com.hospital.dto.InternacaoRequest;
import br.com.hospital.enums.StatusInternacao;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Internacao;
import br.com.hospital.model.Paciente;
import br.com.hospital.model.Profissional;
import br.com.hospital.model.Quarto;
import br.com.hospital.repository.InternacaoRepository;

@Service
public class InternacaoService {

    private final InternacaoRepository repository;
    private final PacienteService pacienteService;
    private final ProfissionalService profissionalService;
    private final QuartoService quartoService;

    public InternacaoService(InternacaoRepository repository, PacienteService pacienteService,
                             ProfissionalService profissionalService, QuartoService quartoService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
        this.profissionalService = profissionalService;
        this.quartoService = quartoService;
    }

    public List<Internacao> listar(StatusInternacao status) {
        return repository.listar().stream()
                .filter(i -> status == null || i.getStatus() == status)
                .toList();
    }

    public Internacao buscar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Internação " + id + " não encontrada."));
    }

    public Internacao internar(InternacaoRequest dados) {
        Paciente paciente = pacienteService.buscar(dados.pacienteId());
        Profissional medico = profissionalService.buscar(dados.medicoId());
        Quarto quarto = quartoService.buscar(dados.quartoId());

        // um paciente nao pode ter duas internacoes ao mesmo tempo
        boolean jaInternado = repository.listar().stream()
                .anyMatch(i -> i.getStatus() == StatusInternacao.EM_ANDAMENTO
                        && i.getPaciente().getId().equals(paciente.getId()));
        if (jaInternado) {
            throw new RegraNegocioException("O paciente já está internado.");
        }

        LocalDate entrada = dados.dataEntrada() != null ? dados.dataEntrada() : LocalDate.now();
        Internacao internacao = new Internacao(paciente, medico, quarto, entrada,
                dados.previsaoAlta(), dados.observacoes());
        internacao.validar();

        quarto.ocupar(); // lanca erro se o quarto estiver lotado
        return repository.salvar(internacao);
    }

    public Internacao atualizar(Long id, AtualizacaoInternacaoRequest dados) {
        Internacao internacao = buscar(id);
        internacao.atualizar(dados.previsaoAlta(), dados.observacoes());
        return repository.salvar(internacao);
    }

    public Internacao trocarQuarto(Long id, Long novoQuartoId) {
        Internacao internacao = buscar(id);
        internacao.trocarQuarto(quartoService.buscar(novoQuartoId));
        return repository.salvar(internacao);
    }

    public Internacao darAlta(Long id, LocalDate dataAlta) {
        Internacao internacao = buscar(id);
        internacao.darAlta(dataAlta != null ? dataAlta : LocalDate.now());
        return repository.salvar(internacao);
    }
}
