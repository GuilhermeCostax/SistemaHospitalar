package br.com.hospital.enums;

public enum Especialidade {
    CARDIOLOGIA("Cardiologia"),
    NEUROLOGIA("Neurologia"),
    ORTOPEDIA("Ortopedia"),
    PEDIATRIA("Pediatria"),
    DERMATOLOGIA("Dermatologia"),
    GINECOLOGIA("Ginecologia");

    private final String descricao;

    Especialidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
