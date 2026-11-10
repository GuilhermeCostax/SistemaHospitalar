package br.com.hospital.enums;

/**
 * Cada tipo de quarto tem uma capacidade maxima e um valor de diaria.
 */
public enum TipoQuarto {
    ENFERMARIA("Enfermaria", 4, 250.0),
    APARTAMENTO("Apartamento", 1, 600.0),
    UTI("UTI", 1, 1500.0);

    private final String descricao;
    private final int capacidadeMaxima;
    private final double valorDiaria;

    TipoQuarto(String descricao, int capacidadeMaxima, double valorDiaria) {
        this.descricao = descricao;
        this.capacidadeMaxima = capacidadeMaxima;
        this.valorDiaria = valorDiaria;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCapacidadeMaxima() {
        return capacidadeMaxima;
    }

    public double getValorDiaria() {
        return valorDiaria;
    }
}
