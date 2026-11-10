package br.com.hospital.model;

import java.time.LocalDate;
import java.time.LocalTime;

import br.com.hospital.enums.Especialidade;
import br.com.hospital.enums.TipoQuarto;

/** Objetos prontos para os testes de dominio. */
final class Fixtures {

    static final LocalDate DATA_FUTURA = LocalDate.now().plusDays(10);
    static final LocalTime HORA = LocalTime.of(9, 0);

    private static long proximoId = 1;

    private Fixtures() {
    }

    static <T extends Entidade> T comId(T entidade) {
        entidade.setId(proximoId++);
        return entidade;
    }

    static Paciente paciente() {
        return comId(new Paciente("Ana", "000.000.000-00", LocalDate.of(1990, 5, 18), null, null, null));
    }

    static ClinicoGeral clinico() {
        return comId(new ClinicoGeral("Dr. Rafael", "CRM-1", null, null));
    }

    static MedicoEspecialista cardiologista() {
        return comId(new MedicoEspecialista("Dra. Helena", "CRM-2", null, null, Especialidade.CARDIOLOGIA));
    }

    static Cirurgiao cirurgiao() {
        return comId(new Cirurgiao("Dr. Paulo", "CRM-3", null, null, "Cirurgia geral"));
    }

    static Quarto quarto(TipoQuarto tipo, int capacidade) {
        return comId(new Quarto("10" + proximoId, "1º andar", tipo, capacidade));
    }
}
