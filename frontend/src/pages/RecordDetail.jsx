import "../styles/pages/RecordDetail.css";
import { Link, useParams } from "react-router-dom";
import {
  BedDouble,
  CalendarDays,
  FileText,
  Mail,
  Phone,
  Stethoscope,
  Users,
} from "lucide-react";
import {
  patients,
  professionals,
  consultations,
  admissions,
  rooms,
  findPatient,
  findProfessional,
  findRoom,
  dateLabel,
} from "../data/mockData";
import {
  Avatar,
  Badge,
  DetailGrid,
  PageHeader,
  Panel,
  PreviewNote,
  RowLink,
} from "../components/UI";
import NotFound from "./NotFound";
const sources = {
  pacientes: patients,
  profissionais: professionals,
  consultas: consultations,
  internacoes: admissions,
  quartos: rooms,
};
export default function RecordDetail({ type }) {
  const { id } = useParams();
  const r = sources[type].find((x) => x.id === id);
  if (!r) return <NotFound />;
  const isPerson = ["pacientes", "profissionais"].includes(type);
  const title = isPerson
    ? r.name
    : type === "quartos"
      ? `Quarto ${r.number}`
      : `${type === "consultas" ? "Consulta" : "Internação"} ${r.code}`;
  const items =
    type === "pacientes"
      ? [
          ["Nome", r.name],
          ["CPF", r.cpf],
          ["Data de nascimento", dateLabel(r.birth)],
          ["Telefone", r.phone],
          ["E-mail", r.email],
          ["Endereço", r.address],
        ]
      : type === "profissionais"
        ? [
            ["Nome", r.name],
            ["Registro profissional", r.registration],
            ["Especialidade", r.specialty],
            ["Telefone", r.phone],
            ["E-mail", r.email],
          ]
        : type === "consultas"
          ? [
              ["Código do atendimento", r.code],
              [
                "Paciente",
                <Link to={`/pacientes/${r.patientId}`}>
                  {findPatient(r.patientId).name}
                </Link>,
              ],
              [
                "Profissional responsável",
                <Link to={`/profissionais/${r.professionalId}`}>
                  {findProfessional(r.professionalId).name}
                </Link>,
              ],
              ["Data", dateLabel(r.date)],
              ["Horário", r.time],
              ["Motivo da consulta", r.reason],
            ]
          : type === "internacoes"
            ? [
                ["Código do atendimento", r.code],
                [
                  "Paciente",
                  <Link to={`/pacientes/${r.patientId}`}>
                    {findPatient(r.patientId).name}
                  </Link>,
                ],
                [
                  "Profissional responsável",
                  <Link to={`/profissionais/${r.professionalId}`}>
                    {findProfessional(r.professionalId).name}
                  </Link>,
                ],
                [
                  "Quarto",
                  <Link to={`/quartos/${r.roomId}`}>
                    {findRoom(r.roomId).number}
                  </Link>,
                ],
                ["Data de entrada", dateLabel(r.entry)],
                ["Data prevista de alta", dateLabel(r.expected)],
                ["Data efetiva de alta", dateLabel(r.discharge)],
              ]
            : [
                ["Número de identificação", r.number],
                ["Andar", r.floor],
                ["Capacidade máxima", `${r.capacity} paciente(s)`],
                ["Ocupação atual", `${r.occupied} paciente(s)`],
                ["Vagas disponíveis", r.capacity - r.occupied],
                ["Situação atual", r.status],
              ];
  return (
    <>
      <PageHeader
        title={title}
        description={
          isPerson
            ? "Informações completas do cadastro e dos atendimentos."
            : "Informações e acompanhamento do registro hospitalar."
        }
        back={`/${type}`}
      />
      <PreviewNote />
      <div className="detail-layout">
        <div>
          <Panel title="Informações do registro">
            <div className="record-hero">
              {isPerson ? (
                <Avatar name={r.name} initials={r.initials} />
              ) : (
                <span className="record-icon">
                  {type === "consultas" ? (
                    <CalendarDays size={28} />
                  ) : (
                    <BedDouble size={28} />
                  )}
                </span>
              )}
              <div>
                <h2>{title}</h2>
                <p>
                  {isPerson
                    ? r.email
                    : type === "quartos"
                      ? r.floor
                      : "Registro de atendimento"}
                </p>
              </div>
              <Badge>{r.status}</Badge>
            </div>
            <DetailGrid items={items} />
          </Panel>
          {r.notes && (
            <Panel title="Observações médicas">
              <p className="notes-text">{r.notes}</p>
            </Panel>
          )}
          {type === "quartos" && (
            <Panel
              title="Pacientes neste quarto"
              subtitle="Internações em andamento"
            >
              {admissions.filter((a) => a.roomId === id && !a.discharge)
                .length ? (
                admissions
                  .filter((a) => a.roomId === id && !a.discharge)
                  .map((a) => (
                    <div className="related-row" key={a.id}>
                      <span>{findPatient(a.patientId).name}</span>
                      <RowLink to={`/internacoes/${a.id}`} label={a.code} />
                    </div>
                  ))
              ) : (
                <p className="notes-text">
                  Nenhum paciente internado neste quarto.
                </p>
              )}
            </Panel>
          )}
        </div>
        <div>
          <Panel title="Ações do registro">
            <div className="action-list">
              <Link to={`/${type}/${id}/editar`}>
                <FileText size={18} />
                Editar informações
              </Link>
              {type === "pacientes" && (
                <>
                  <Link to={`/historico/${id}`}>
                    <FileText size={18} />
                    Histórico médico
                  </Link>
                  <Link to="/consultas/novo">
                    <CalendarDays size={18} />
                    Agendar consulta
                  </Link>
                  <Link to="/internacoes/novo">
                    <BedDouble size={18} />
                    Registrar internação
                  </Link>
                </>
              )}
              {type === "profissionais" && (
                <Link to="/disponibilidade">
                  <Stethoscope size={18} />
                  Ver disponibilidade
                </Link>
              )}
              {type === "consultas" &&
                r.status !== "Concluída" &&
                r.status !== "Cancelada" && (
                  <>
                    <Link to={`/consultas/${id}/remarcar`}>
                      <CalendarDays size={18} />
                      Remarcar consulta
                    </Link>
                    <Link to={`/consultas/${id}/finalizar`}>
                      <FileText size={18} />
                      Finalizar atendimento
                    </Link>
                    <Link
                      className="danger-link"
                      to={`/consultas/${id}/cancelar`}
                    >
                      Cancelar consulta
                    </Link>
                  </>
                )}
              {type === "internacoes" && !r.discharge && (
                <>
                  <Link to={`/internacoes/${id}/trocar-quarto`}>
                    <BedDouble size={18} />
                    Trocar quarto
                  </Link>
                  <Link to={`/internacoes/${id}/alta`}>
                    <Users size={18} />
                    Registrar alta
                  </Link>
                </>
              )}
              <button disabled title="Ação disponível em uma próxima etapa">
                Excluir registro
              </button>
            </div>
          </Panel>
          <div className="info-card">
            <span className="icon-box teal">
              <FileText size={20} />
            </span>
            <h3>Informação que acompanha.</h3>
            <p>
              O histórico reúne consultas, internações e observações em um só
              lugar.
            </p>
          </div>
        </div>
      </div>
    </>
  );
}
