package br.com.hospital.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.hospital.dto.QuartoRequest;
import br.com.hospital.model.Quarto;
import br.com.hospital.service.QuartoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/quartos")
public class QuartoController {

    private final QuartoService service;

    public QuartoController(QuartoService service) {
        this.service = service;
    }

    // GET /quartos?disponiveis=true mostra so quartos com vaga
    @GetMapping
    public List<Quarto> listar(@RequestParam(defaultValue = "false") boolean disponiveis) {
        return service.listar(disponiveis);
    }

    @GetMapping("/{id}")
    public Quarto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Quarto cadastrar(@Valid @RequestBody QuartoRequest dados) {
        return service.cadastrar(dados);
    }

    // a capacidade nao pode ficar menor que a ocupacao atual
    @PutMapping("/{id}")
    public Quarto atualizar(@PathVariable Long id, @Valid @RequestBody QuartoRequest dados) {
        return service.atualizar(id, dados);
    }

    // so remove quartos sem pacientes internados
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id) {
        service.remover(id);
    }
}
