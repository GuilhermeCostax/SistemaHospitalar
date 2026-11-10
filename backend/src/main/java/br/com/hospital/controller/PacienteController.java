package br.com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.hospital.dto.PacienteRequest;
import br.com.hospital.model.Paciente;
import br.com.hospital.service.PacienteService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService service;

    public PacienteController(PacienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Paciente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Paciente buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Paciente cadastrar(@Valid @RequestBody PacienteRequest dados) {
        return service.cadastrar(dados);
    }

    @PutMapping("/{id}")
    public Paciente atualizar(@PathVariable Long id, @Valid @RequestBody PacienteRequest dados) {
        return service.atualizar(id, dados);
    }
}
