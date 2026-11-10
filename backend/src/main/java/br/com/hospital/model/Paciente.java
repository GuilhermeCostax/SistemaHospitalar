package br.com.hospital.model;

import java.time.LocalDate;

public class Paciente extends Pessoa {

    private String cpf;
    private LocalDate dataNascimento;
    private String endereco;

    public Paciente(String nome, String cpf, LocalDate dataNascimento, String telefone, String email, String endereco) {
        super(nome, telefone, email);
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
    }

    public String getCpf() {
        return cpf;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }
}
