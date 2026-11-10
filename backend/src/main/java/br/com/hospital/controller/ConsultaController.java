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

import br.com.hospital.dto.ConsultaRequest;
import br.com.hospital.dto.EncaminhamentoRequest;
import br.com.hospital.dto.ObservacoesRequest;
import br.com.hospital.dto.RemarcacaoRequest;
import br.com.hospital.dto.SituacaoRequest;
import br.com.hospital.enums.SituacaoConsulta;
import br.com.hospital.model.Consulta;
import br.com.hospital.service.ConsultaService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService service;

    public ConsultaController(ConsultaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Consulta> listar(@RequestParam(required = false) SituacaoConsulta situacao) {
        return service.listar(situacao);
    }

    @GetMapping("/{id}")
    public Consulta buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta agendar(@Valid @RequestBody ConsultaRequest dados) {
        return service.agendar(dados);
    }

    @PutMapping("/{id}/situacao")
    public Consulta mudarSituacao(@PathVariable Long id, @Valid @RequestBody SituacaoRequest dados) {
        return service.mudarSituacao(id, dados.situacao());
    }

    @PutMapping("/{id}/observacoes")
    public Consulta registrarObservacoes(@PathVariable Long id, @Valid @RequestBody ObservacoesRequest dados) {
        return service.registrarObservacoes(id, dados.observacoes());
    }

    @PostMapping("/{id}/encaminhamento")
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta encaminhar(@PathVariable Long id, @Valid @RequestBody EncaminhamentoRequest dados) {
        return service.encaminhar(id, dados);
    }

    // gera um novo agendamento; a consulta original fica cancelada e ligada a nova
    @PostMapping("/{id}/remarcar")
    @ResponseStatus(HttpStatus.CREATED)
    public Consulta remarcar(@PathVariable Long id, @Valid @RequestBody RemarcacaoRequest dados) {
        return service.remarcar(id, dados.data(), dados.hora());
    }
}
