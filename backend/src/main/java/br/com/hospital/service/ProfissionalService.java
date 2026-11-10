package br.com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.ProfissionalRequest;
import br.com.hospital.enums.Especialidade;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.ClinicoGeral;
import br.com.hospital.model.MedicoEspecialista;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.model.Profissional;
import br.com.hospital.repository.ProfissionalRepository;

@Service
public class ProfissionalService {

    private final ProfissionalRepository repository;

    public ProfissionalService(ProfissionalRepository repository) {
        this.repository = repository;
    }

    public List<Profissional> listar() {
        return repository.listar();
    }

    /** Lista os especialistas de uma area de atuacao. */
    public List<Profissional> listarPorEspecialidade(Especialidade especialidade) {
        return repository.listar().stream()
                .filter(p -> p instanceof MedicoEspecialista)
                .filter(p -> ((MedicoEspecialista) p).getEspecialidade() == especialidade)
                .toList();
    }

    public Profissional buscar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Profissional " + id + " não encontrado."));
    }

    public Cirurgiao buscarCirurgiao(Long id) {
        Profissional profissional = buscar(id);
        if (!(profissional instanceof Cirurgiao)) {
            throw new RegraNegocioException("O profissional " + profissional.getNome() + " não é cirurgião.");
        }
        return (Cirurgiao) profissional;
    }

    public Profissional cadastrar(ProfissionalRequest dados) {
        boolean registroRepetido = repository.listar().stream()
                .anyMatch(p -> p.getRegistro().equalsIgnoreCase(dados.registro()));
        if (registroRepetido) {
            throw new RegraNegocioException("Já existe um profissional com este registro.");
        }

        Profissional profissional;
        switch (dados.categoria()) {
            case CLINICO_GERAL:
                profissional = new ClinicoGeral(dados.nome(), dados.registro(), dados.telefone(), dados.email());
                break;
            case ESPECIALISTA:
                if (dados.especialidade() == null) {
                    throw new RegraNegocioException("Médico especialista precisa informar a especialidade.");
                }
                profissional = new MedicoEspecialista(dados.nome(), dados.registro(), dados.telefone(),
                        dados.email(), dados.especialidade());
                break;
            case CIRURGIAO:
                if (dados.areaCirurgica() == null || dados.areaCirurgica().isBlank()) {
                    throw new RegraNegocioException("Cirurgião precisa informar a área de atuação cirúrgica.");
                }
                profissional = new Cirurgiao(dados.nome(), dados.registro(), dados.telefone(),
                        dados.email(), dados.areaCirurgica());
                break;
            default:
                throw new RegraNegocioException("Categoria inválida.");
        }
        return repository.salvar(profissional);
    }

    public List<ProcedimentoCirurgico> listarProcedimentos(Long cirurgiaoId) {
        return buscarCirurgiao(cirurgiaoId).getProcedimentos();
    }
}
