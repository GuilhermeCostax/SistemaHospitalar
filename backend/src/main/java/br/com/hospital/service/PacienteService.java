package br.com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.PacienteRequest;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Paciente;
import br.com.hospital.repository.CirurgiaRepository;
import br.com.hospital.repository.ConsultaRepository;
import br.com.hospital.repository.EmergenciaRepository;
import br.com.hospital.repository.InternacaoRepository;
import br.com.hospital.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository repository;
    private final ConsultaRepository consultaRepository;
    private final InternacaoRepository internacaoRepository;
    private final CirurgiaRepository cirurgiaRepository;
    private final EmergenciaRepository emergenciaRepository;

    public PacienteService(PacienteRepository repository, ConsultaRepository consultaRepository,
                           InternacaoRepository internacaoRepository, CirurgiaRepository cirurgiaRepository,
                           EmergenciaRepository emergenciaRepository) {
        this.repository = repository;
        this.consultaRepository = consultaRepository;
        this.internacaoRepository = internacaoRepository;
        this.cirurgiaRepository = cirurgiaRepository;
        this.emergenciaRepository = emergenciaRepository;
    }

    public List<Paciente> listar() {
        return repository.listar();
    }

    public Paciente buscar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Paciente " + id + " não encontrado."));
    }

    public Paciente cadastrar(PacienteRequest dados) {
        // nao pode haver dois pacientes com o mesmo CPF
        boolean cpfRepetido = repository.listar().stream()
                .anyMatch(p -> p.getCpf().equals(dados.cpf()));
        if (cpfRepetido) {
            throw new RegraNegocioException("Já existe um paciente cadastrado com este CPF.");
        }
        Paciente paciente = new Paciente(dados.nome(), dados.cpf(), dados.dataNascimento(),
                dados.telefone(), dados.email(), dados.endereco());
        return repository.salvar(paciente);
    }

    public Paciente atualizar(Long id, PacienteRequest dados) {
        Paciente paciente = buscar(id);
        // o CPF nao pode ser alterado
        if (!paciente.getCpf().equals(dados.cpf())) {
            throw new RegraNegocioException("O CPF de um paciente não pode ser alterado.");
        }
        paciente.setNome(dados.nome());
        paciente.setDataNascimento(dados.dataNascimento());
        paciente.setTelefone(dados.telefone());
        paciente.setEmail(dados.email());
        paciente.setEndereco(dados.endereco());
        return repository.salvar(paciente);
    }

    /**
     * O historico medico precisa ser preservado: so e possivel remover
     * pacientes que nunca tiveram consulta, internacao, cirurgia ou emergencia.
     */
    public void remover(Long id) {
        buscar(id);
        boolean temHistorico =
                consultaRepository.listar().stream().anyMatch(c -> c.getPaciente().getId().equals(id))
                || internacaoRepository.listar().stream().anyMatch(i -> i.getPaciente().getId().equals(id))
                || cirurgiaRepository.listar().stream().anyMatch(c -> c.getPaciente().getId().equals(id))
                || emergenciaRepository.listar().stream().anyMatch(e -> e.getPaciente().getId().equals(id));
        if (temHistorico) {
            throw new RegraNegocioException("O paciente possui histórico de atendimentos e não pode ser removido.");
        }
        repository.remover(id);
    }
}
