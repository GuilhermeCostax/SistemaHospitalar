package br.com.hospital.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.hospital.dto.ProfissionalRequest;
import br.com.hospital.enums.CategoriaProfissional;
import br.com.hospital.enums.Especialidade;
import br.com.hospital.model.ProcedimentoCirurgico;
import br.com.hospital.model.Profissional;
import br.com.hospital.service.DisponibilidadeService;
import br.com.hospital.service.ProfissionalService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/profissionais")
public class ProfissionalController {

    private final ProfissionalService service;
    private final DisponibilidadeService disponibilidadeService;

    public ProfissionalController(ProfissionalService service, DisponibilidadeService disponibilidadeService) {
        this.service = service;
        this.disponibilidadeService = disponibilidadeService;
    }

    // GET /profissionais  ou  GET /profissionais?especialidade=CARDIOLOGIA
    @GetMapping
    public List<Profissional> listar(@RequestParam(required = false) Especialidade especialidade) {
        if (especialidade != null) {
            return service.listarPorEspecialidade(especialidade);
        }
        return service.listar();
    }

    // GET /profissionais/disponiveis?especialidade=CARDIOLOGIA&data=2027-01-10&hora=09:00
    // todos os filtros sao opcionais; data e hora precisam vir juntas
    @GetMapping("/disponiveis")
    public List<Profissional> disponiveis(@RequestParam(required = false) Especialidade especialidade,
                                          @RequestParam(required = false) CategoriaProfissional categoria,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora) {
        return disponibilidadeService.listarDisponiveis(especialidade, categoria, data, hora);
    }

    @GetMapping("/{id}")
    public Profissional buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profissional cadastrar(@Valid @RequestBody ProfissionalRequest dados) {
        return service.cadastrar(dados);
    }

    // procedimentos de um cirurgiao (data, paciente e descricao)
    @GetMapping("/{id}/procedimentos")
    public List<ProcedimentoCirurgico> procedimentos(@PathVariable Long id) {
        return service.listarProcedimentos(id);
    }
}
