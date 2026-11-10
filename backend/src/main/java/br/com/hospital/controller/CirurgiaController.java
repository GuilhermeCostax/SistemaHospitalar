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

import br.com.hospital.dto.CirurgiaRequest;
import br.com.hospital.dto.ObservacoesRequest;
import br.com.hospital.enums.StatusCirurgia;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.service.CirurgiaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cirurgias")
public class CirurgiaController {

    private final CirurgiaService service;

    public CirurgiaController(CirurgiaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProcedimentoCirurgico> listar(@RequestParam(required = false) StatusCirurgia status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    public ProcedimentoCirurgico buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedimentoCirurgico agendar(@Valid @RequestBody CirurgiaRequest dados) {
        return service.agendar(dados);
    }

    @PutMapping("/{id}/iniciar")
    public ProcedimentoCirurgico iniciar(@PathVariable Long id) {
        return service.iniciar(id);
    }

    @PutMapping("/{id}/concluir")
    public ProcedimentoCirurgico concluir(@PathVariable Long id) {
        return service.concluir(id);
    }

    @PutMapping("/{id}/cancelar")
    public ProcedimentoCirurgico cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }

    @PutMapping("/{id}/pos-operatorio")
    public ProcedimentoCirurgico posOperatorio(@PathVariable Long id, @Valid @RequestBody ObservacoesRequest dados) {
        return service.registrarPosOperatorio(id, dados.observacoes());
    }
}
