package br.com.hospital.model;

import br.com.hospital.enums.Especialidade;

public class MedicoEspecialista extends Profissional {

    private Especialidade especialidade;

    public MedicoEspecialista(String nome, String registro, String telefone, String email, Especialidade especialidade) {
        super(nome, registro, telefone, email);
        this.especialidade = especialidade;
    }

    public Especialidade getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(Especialidade especialidade) {
        this.especialidade = especialidade;
    }

    @Override
    public String getCategoria() {
        return "ESPECIALISTA";
    }
}
