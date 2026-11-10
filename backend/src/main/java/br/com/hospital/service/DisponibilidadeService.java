package br.com.hospital.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.hospital.enums.CategoriaProfissional;
import br.com.hospital.enums.Especialidade;
import br.com.hospital.exception.RegraNegocioException;
import br.com.hospital.model.Cirurgiao;
import br.com.hospital.model.MedicoEspecialista;
import br.com.hospital.model.Profissional;

/**
 * Responde quais profissionais estao livres em um dia e horario,
 * filtrando por area de atuacao (especialidade) ou categoria.
 * Fica separado do ProfissionalService para nao criar dependencia
 * circular com o ConsultaService.
 */
@Service
public class DisponibilidadeService {

    private final ProfissionalService profissionalService;
    private final ConsultaService consultaService;

    public DisponibilidadeService(ProfissionalService profissionalService, ConsultaService consultaService) {
        this.profissionalService = profissionalService;
        this.consultaService = consultaService;
    }

    public List<Profissional> listarDisponiveis(Especialidade especialidade, CategoriaProfissional categoria,
                                                LocalDate data, LocalTime hora) {
        if ((data == null) != (hora == null)) {
            throw new RegraNegocioException("Informe data e hora juntas para consultar a disponibilidade.");
        }
        return profissionalService.listar().stream()
                .filter(p -> categoria == null || p.getCategoria().equals(categoria.name()))
                .filter(p -> especialidade == null
                        || (p instanceof MedicoEspecialista m && m.getEspecialidade() == especialidade))
                .filter(p -> data == null || estaLivre(p, data, hora))
                .toList();
    }

    public boolean estaLivre(Profissional profissional, LocalDate data, LocalTime hora) {
        if (consultaService.medicoTemConsulta(profissional.getId(), data, hora)) {
            return false;
        }
        return !(profissional instanceof Cirurgiao c && c.temProcedimentoEm(data, hora));
    }
}
