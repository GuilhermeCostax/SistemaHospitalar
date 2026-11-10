package br.com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.hospital.dto.EmergenciaRequest;
import br.com.hospital.dto.IniciarEmergenciaRequest;
import br.com.hospital.enums.StatusEmergencia;
import br.com.hospital.model.AtendimentoEmergencia;
import br.com.hospital.service.EmergenciaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/emergencias")
public class EmergenciaController {

    private final EmergenciaService service;

    public EmergenciaController(EmergenciaService service) {
        this.service = service;
    }

    // ?status=AGUARDANDO_AVALIACAO mostra a fila de espera
    @GetMapping
    public List<AtendimentoEmergencia> listar(@RequestParam(required = false) StatusEmergencia status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    public AtendimentoEmergencia buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AtendimentoEmergencia registrar(@Valid @RequestBody EmergenciaRequest dados) {
        return service.registrar(dados);
    }

    @PutMapping("/{id}/iniciar")
    public AtendimentoEmergencia iniciar(@PathVariable Long id, @Valid @RequestBody IniciarEmergenciaRequest dados) {
        return service.iniciar(id, dados.medicoId());
    }

    @PutMapping("/{id}/concluir")
    public AtendimentoEmergencia concluir(@PathVariable Long id) {
        return service.concluir(id);
    }
}
