package br.com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.PacienteRequest;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Paciente;
import br.com.hospital.repository.PacienteRepository;

@Service
public class PacienteService {

    private final PacienteRepository repository;

    public PacienteService(PacienteRepository repository) {
        this.repository = repository;
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
}
