package br.com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.ProfissionalRequest;
import br.com.hospital.enums.Especialidade;
import br.com.hospital.enums.StatusEmergencia;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.ClinicoGeral;
import br.com.hospital.model.MedicoEspecialista;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.model.Profissional;
import br.com.hospital.repository.ConsultaRepository;
import br.com.hospital.repository.EmergenciaRepository;
import br.com.hospital.repository.InternacaoRepository;
import br.com.hospital.repository.ProfissionalRepository;

@Service
public class ProfissionalService {

    private final ProfissionalRepository repository;
    private final ConsultaRepository consultaRepository;
    private final InternacaoRepository internacaoRepository;
    private final EmergenciaRepository emergenciaRepository;

    public ProfissionalService(ProfissionalRepository repository, ConsultaRepository consultaRepository,
                               InternacaoRepository internacaoRepository, EmergenciaRepository emergenciaRepository) {
        this.repository = repository;
        this.consultaRepository = consultaRepository;
        this.internacaoRepository = internacaoRepository;
        this.emergenciaRepository = emergenciaRepository;
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
        validarRegistroUnico(dados.registro(), null);

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

    /**
     * Atualiza os dados de um profissional. A categoria nao muda (seria outra classe);
     * para isso o profissional deve ser removido e cadastrado de novo.
     */
    public Profissional atualizar(Long id, ProfissionalRequest dados) {
        Profissional profissional = buscar(id);
        if (!profissional.getCategoria().equals(dados.categoria().name())) {
            throw new RegraNegocioException("A categoria de um profissional não pode ser alterada.");
        }
        validarRegistroUnico(dados.registro(), id);

        if (profissional instanceof MedicoEspecialista especialista) {
            if (dados.especialidade() == null) {
                throw new RegraNegocioException("Médico especialista precisa informar a especialidade.");
            }
            if (dados.especialidade() != especialista.getEspecialidade() && temConsultasAtivas(id)) {
                throw new RegraNegocioException("Não é possível mudar a especialidade de um médico com consultas ativas.");
            }
            especialista.setEspecialidade(dados.especialidade());
        } else if (profissional instanceof Cirurgiao cirurgiao) {
            if (dados.areaCirurgica() == null || dados.areaCirurgica().isBlank()) {
                throw new RegraNegocioException("Cirurgião precisa informar a área de atuação cirúrgica.");
            }
            cirurgiao.setAreaCirurgica(dados.areaCirurgica());
        }
        profissional.setNome(dados.nome());
        profissional.setRegistro(dados.registro());
        profissional.setTelefone(dados.telefone());
        profissional.setEmail(dados.email());
        return repository.salvar(profissional);
    }

    /** So remove profissionais sem atendimentos ativos ou pendentes. */
    public void remover(Long id) {
        Profissional profissional = buscar(id);
        if (temConsultasAtivas(id)) {
            throw new RegraNegocioException("O profissional possui consultas ativas.");
        }
        boolean temInternacao = internacaoRepository.listar().stream()
                .anyMatch(i -> i.estaEmAndamento() && i.getMedico().getId().equals(id));
        if (temInternacao) {
            throw new RegraNegocioException("O profissional é responsável por internações em andamento.");
        }
        boolean temEmergencia = emergenciaRepository.listar().stream()
                .anyMatch(e -> e.getStatus() == StatusEmergencia.EM_ATENDIMENTO
                        && e.getMedico() != null && e.getMedico().getId().equals(id));
        if (temEmergencia) {
            throw new RegraNegocioException("O profissional está em um atendimento de emergência.");
        }
        if (profissional instanceof Cirurgiao cirurgiao && cirurgiao.temProcedimentosPendentes()) {
            throw new RegraNegocioException("O cirurgião possui procedimentos agendados ou em andamento.");
        }
        repository.remover(id);
    }

    private boolean temConsultasAtivas(Long medicoId) {
        return consultaRepository.listar().stream()
                .anyMatch(c -> c.getSituacao().estaAtiva() && c.getMedico().getId().equals(medicoId));
    }

    // idIgnorado: na atualizacao o proprio profissional nao conta como repetido
    private void validarRegistroUnico(String registro, Long idIgnorado) {
        boolean registroRepetido = repository.listar().stream()
                .filter(p -> !p.getId().equals(idIgnorado))
                .anyMatch(p -> p.getRegistro().equalsIgnoreCase(registro));
        if (registroRepetido) {
            throw new RegraNegocioException("Já existe um profissional com este registro.");
        }
    }

    public List<ProcedimentoCirurgico> listarProcedimentos(Long cirurgiaoId) {
        return buscarCirurgiao(cirurgiaoId).getProcedimentos();
    }
}
