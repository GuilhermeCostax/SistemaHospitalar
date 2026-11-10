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

import br.com.hospital.dto.AltaRequest;
import br.com.hospital.dto.InternacaoRequest;
import br.com.hospital.dto.TrocaQuartoRequest;
import br.com.hospital.enums.StatusInternacao;
import br.com.hospital.model.Internacao;
import br.com.hospital.service.InternacaoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/internacoes")
public class InternacaoController {

    private final InternacaoService service;

    public InternacaoController(InternacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Internacao> listar(@RequestParam(required = false) StatusInternacao status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    public Internacao buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Internacao internar(@Valid @RequestBody InternacaoRequest dados) {
        return service.internar(dados);
    }

    @PutMapping("/{id}/trocar-quarto")
    public Internacao trocarQuarto(@PathVariable Long id, @Valid @RequestBody TrocaQuartoRequest dados) {
        return service.trocarQuarto(id, dados.quartoId());
    }

    // corpo opcional: {"dataAlta": "2026-12-10"}
    @PutMapping("/{id}/alta")
    public Internacao darAlta(@PathVariable Long id, @RequestBody(required = false) AltaRequest dados) {
        return service.darAlta(id, dados == null ? null : dados.dataAlta());
    }
}
