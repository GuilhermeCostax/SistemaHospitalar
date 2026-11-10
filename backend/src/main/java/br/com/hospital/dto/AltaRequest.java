package br.com.hospital.dto;

import java.time.LocalDate;

/** Se dataAlta nao for enviada, usamos a data de hoje. */
public record AltaRequest(LocalDate dataAlta) {
}
