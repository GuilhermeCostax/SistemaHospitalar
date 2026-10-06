import "../styles/pages/History.css";
import { Link, useParams } from "react-router-dom";
import { BedDouble, CalendarDays, ChevronRight, FileText } from "lucide-react";
import {
  patients,
  consultations,
  admissions,
  findPatient,
  findProfessional,
  dateLabel,
} from "../data/mockData";
import {
  Avatar,
  Badge,
  DetailGrid,
  PageHeader,
  Panel,
  Person,
  PreviewNote,
  RowLink,
  Table,
  Toolbar,
} from "../components/UI";
import NotFound from "./NotFound";
export default function History() {
  const { id } = useParams();
  const patient = id ? findPatient(id) : null;
  if (id && !patient) return <NotFound />;
  if (!id)
    return (
      <>
        <PageHeader
          title="Histórico médico"
          description="A jornada de cuidado de cada paciente, reunida em um só lugar."
        />
        <PreviewNote />
        <Panel
          title="Selecione um paciente"
          subtitle="Consulte os registros de consultas e internações"
        >
          <Toolbar placeholder="Buscar paciente por nome ou CPF" />
          <Table
            columns={["Paciente", "CPF", "Último atendimento", ""]}
            rows={patients}
            renderRow={(p) => (
              <tr key={p.id}>
                <td>
                  <Person {...p} secondary={p.email} />
                </td>
                <td>{p.cpf}</td>
                <td>{p.last}</td>
                <td>
                  <RowLink
                    to={`/historico/${p.id}`}
                    label="Consultar histórico"
                  />
                </td>
              </tr>
            )}
          />
        </Panel>
      </>
    );
  const events = [
    ...consultations
      .filter((c) => c.patientId === id)
      .map((c) => ({
        ...c,
        eventDate: c.date,
        type: "consulta",
        to: `/consultas/${c.id}`,
        text: c.reason,
      })),
    ...admissions
      .filter((a) => a.patientId === id)
      .map((a) => ({
        ...a,
        eventDate: a.entry,
        type: "internação",
        to: `/internacoes/${a.id}`,
        text: a.discharge
          ? `Internação concluída · Alta em ${dateLabel(a.discharge)}`
          : "Internação em andamento",
      })),
  ].sort((a, b) => b.eventDate.localeCompare(a.eventDate));
  return (
    <>
      <PageHeader
        title="Histórico de atendimentos"
        description="Consultas, internações e informações registradas durante o cuidado."
        back="/historico"
      />
      <PreviewNote />
      <div className="history-profile">
        <Avatar {...patient} />
        <div>
          <h2>{patient.name}</h2>
          <p>
            CPF: {patient.cpf} · Nascimento: {dateLabel(patient.birth)}
          </p>
        </div>
        <Link to={`/pacientes/${id}`} className="button secondary">
          Ver cadastro
          <ChevronRight size={16} />
        </Link>
      </div>
      <Panel
        title="Linha do tempo"
        subtitle={`${events.length} registros de atendimento`}
      >
        <div className="timeline">
          {events.length ? (
            events.map((e) => (
              <article className="timeline-event" key={`${e.type}-${e.id}`}>
                <span
                  className={`timeline-icon ${e.type === "consulta" ? "teal" : "blue"}`}
                >
                  {e.type === "consulta" ? (
                    <CalendarDays size={19} />
                  ) : (
                    <BedDouble size={19} />
                  )}
                </span>
                <div className="event-content">
                  <div className="event-top">
                    <span>
                      {dateLabel(e.eventDate)}
                      {e.time && ` · ${e.time}`}
                    </span>
                    <Badge>{e.status}</Badge>
                  </div>
                  <h3>
                    {e.type === "consulta" ? "Consulta" : "Internação"} ·{" "}
                    {e.code}
                  </h3>
                  <p>{e.text}</p>
                  <small>{findProfessional(e.professionalId).name}</small>
                  <div className="event-note">
                    <FileText size={15} />
                    <p>{e.notes}</p>
                  </div>
                  <RowLink to={e.to} label="Ver atendimento completo" />
                </div>
              </article>
            ))
          ) : (
            <div className="empty-history">
              <FileText size={30} />
              <h3>Nenhum atendimento registrado</h3>
              <p>
                O histórico deste paciente ainda não possui consultas ou
                internações no exemplo.
              </p>
            </div>
          )}
        </div>
      </Panel>
    </>
  );
}
