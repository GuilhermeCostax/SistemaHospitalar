package br.com.hospital.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import br.com.hospital.model.Entidade;

/**
 * Repositorio generico que guarda os objetos em memoria (um Map).
 * Nesta sprint nao usamos banco de dados, entao os dados somem quando a aplicacao reinicia.
 */
public abstract class RepositorioMemoria<T extends Entidade> {

    private final Map<Long, T> dados = new LinkedHashMap<>();
    private long proximoId = 1;

    public synchronized T salvar(T objeto) {
        if (objeto.getId() == null) {
            objeto.setId(proximoId++);
        }
        dados.put(objeto.getId(), objeto);
        return objeto;
    }

    public synchronized Optional<T> buscarPorId(Long id) {
        return Optional.ofNullable(dados.get(id));
    }

    public synchronized List<T> listar() {
        return new ArrayList<>(dados.values());
    }

    public synchronized void remover(Long id) {
        dados.remove(id);
    }
}
