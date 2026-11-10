package br.com.hospital.model;

import br.com.hospital.enums.TipoQuarto;
import br.com.hospital.exception.RegraNegocioException;

public class Quarto extends Entidade {

    private String numero;
    private String andar;
    private TipoQuarto tipo;
    private int capacidade;
    private int ocupacao = 0;

    public Quarto(String numero, String andar, TipoQuarto tipo, int capacidade) {
        validarCapacidade(tipo, capacidade);
        this.numero = numero;
        this.andar = andar;
        this.tipo = tipo;
        this.capacidade = capacidade;
    }

    private void validarCapacidade(TipoQuarto tipo, int capacidade) {
        if (capacidade < 1 || capacidade > tipo.getCapacidadeMaxima()) {
            throw new RegraNegocioException("Quarto do tipo " + tipo.getDescricao()
                    + " aceita capacidade de 1 a " + tipo.getCapacidadeMaxima() + ".");
        }
    }

    /** Atualiza os dados do quarto sem deixar a capacidade ficar menor que a ocupacao atual. */
    public void atualizar(String numero, String andar, TipoQuarto tipo, int capacidade) {
        validarCapacidade(tipo, capacidade);
        if (capacidade < ocupacao) {
            throw new RegraNegocioException("O quarto " + this.numero + " tem " + ocupacao
                    + " paciente(s) internado(s); a capacidade não pode ser menor que isso.");
        }
        this.numero = numero;
        this.andar = andar;
        this.tipo = tipo;
        this.capacidade = capacidade;
    }

    public boolean estaOcupado() {
        return ocupacao > 0;
    }

    public boolean temVaga() {
        return ocupacao < capacidade;
    }

    public void ocupar() {
        if (!temVaga()) {
            throw new RegraNegocioException("O quarto " + numero + " está lotado.");
        }
        ocupacao++;
    }

    public void liberar() {
        if (ocupacao > 0) {
            ocupacao--;
        }
    }

    public String getNumero() {
        return numero;
    }

    public String getAndar() {
        return andar;
    }

    public TipoQuarto getTipo() {
        return tipo;
    }

    public int getCapacidade() {
        return capacidade;
    }

    public int getOcupacao() {
        return ocupacao;
    }

    public int getVagasDisponiveis() {
        return capacidade - ocupacao;
    }

    public boolean isDisponivel() {
        return temVaga();
    }

    public double getValorDiaria() {
        return tipo.getValorDiaria();
    }
}
