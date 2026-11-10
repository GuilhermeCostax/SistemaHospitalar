package br.com.hospital.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.EmergenciaRequest;
import br.com.hospital.enums.StatusEmergencia;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.repository.EmergenciaRepository;

@Service
public class EmergenciaService {

    private final EmergenciaRepository repository;
    private final PacienteService pacienteService;
    private final ProfissionalService profissionalService;

    public EmergenciaService(EmergenciaRepository repository, PacienteService pacienteService,
                             ProfissionalService profissionalService) {
        this.repository = repository;
        this.pacienteService = pacienteService;
        this.profissionalService = profissionalService;
    }

    /** Lista da mais prioritaria para a menos prioritaria; empate resolve por ordem de chegada. */
    public List<AtendimentoEmergencia> listar(StatusEmergencia status) {
        return repository.listar().stream()
                .filter(e -> status == null || e.getStatus() == status)
                .sorted(Comparator.comparingInt((AtendimentoEmergencia e) -> e.getPrioridade().getNivel()).reversed()
                        .thenComparing(AtendimentoEmergencia::getId))
                .toList();
    }

    public AtendimentoEmergencia buscar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Atendimento de emergência " + id + " não encontrado."));
    }

    public AtendimentoEmergencia registrar(EmergenciaRequest dados) {
        AtendimentoEmergencia emergencia = new AtendimentoEmergencia(
                pacienteService.buscar(dados.pacienteId()), dados.prioridade(), dados.queixa());
        emergencia.validarCondicoes();
        return repository.salvar(emergencia);
    }

    public AtendimentoEmergencia iniciar(Long id, Long medicoId) {
        AtendimentoEmergencia emergencia = buscar(id);
        emergencia.iniciar(profissionalService.buscar(medicoId));
        return repository.salvar(emergencia);
    }

    public AtendimentoEmergencia concluir(Long id) {
        AtendimentoEmergencia emergencia = buscar(id);
        emergencia.concluir();
        return repository.salvar(emergencia);
    }
}
