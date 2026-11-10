package br.com.hospital.model;

public class ClinicoGeral extends Profissional {

    public ClinicoGeral(String nome, String registro, String telefone, String email) {
        super(nome, registro, telefone, email);
    }

    @Override
    public String getCategoria() {
        return "CLINICO_GERAL";
    }
}
