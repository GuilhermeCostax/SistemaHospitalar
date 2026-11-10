package br.com.hospital.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.dto.QuartoRequest;
import br.com.hospital.exception.NaoEncontradoException;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Quarto;
import br.com.hospital.repository.QuartoRepository;

@Service
public class QuartoService {

    private final QuartoRepository repository;

    public QuartoService(QuartoRepository repository) {
        this.repository = repository;
    }

    public List<Quarto> listar(boolean apenasDisponiveis) {
        return repository.listar().stream()
                .filter(q -> !apenasDisponiveis || q.temVaga())
                .toList();
    }

    public Quarto buscar(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Quarto " + id + " não encontrado."));
    }

    public Quarto cadastrar(QuartoRequest dados) {
        validarNumeroUnico(dados.numero(), null);
        // o construtor do Quarto confere a capacidade em relacao ao tipo
        Quarto quarto = new Quarto(dados.numero(), dados.andar(), dados.tipo(), dados.capacidade());
        return repository.salvar(quarto);
    }

    public Quarto atualizar(Long id, QuartoRequest dados) {
        Quarto quarto = buscar(id);
        validarNumeroUnico(dados.numero(), id);
        quarto.atualizar(dados.numero(), dados.andar(), dados.tipo(), dados.capacidade());
        return repository.salvar(quarto);
    }

    public void remover(Long id) {
        Quarto quarto = buscar(id);
        if (quarto.estaOcupado()) {
            throw new RegraNegocioException("O quarto " + quarto.getNumero() + " possui pacientes internados.");
        }
        repository.remover(id);
    }

    private void validarNumeroUnico(String numero, Long idIgnorado) {
        boolean numeroRepetido = repository.listar().stream()
                .filter(q -> !q.getId().equals(idIgnorado))
                .anyMatch(q -> q.getNumero().equals(numero));
        if (numeroRepetido) {
            throw new RegraNegocioException("Já existe um quarto com o número " + numero + ".");
        }
    }
}
