import { patients, professionals, consultations, admissions, findPatient, findProfessional, findRoom, dateLabel } from "../data/mockData";
import { Badge, PageHeader, Panel, Person, PreviewNote, RowLink, Table, Toolbar } from "../components/UI";
import "../styles/pages/Directory.css";

const config = {
  pacientes: {
    title: "Pacientes",
    description: "Dados organizados para um cuidado mais próximo.",
    action: "Cadastrar paciente",
    columns: [
      "Paciente",
      "CPF",
      "Telefone",
      "Último atendimento",
      "Situação",
      "",
    ],
    data: patients,
  },
  profissionais: {
    title: "Profissionais da saúde",
    description: "Conheça a equipe responsável por cada atendimento.",
    action: "Cadastrar profissional",
    columns: [
      "Profissional",
      "Registro profissional",
      "Especialidade",
      "Disponibilidade",
      "",
    ],
    data: professionals,
  },
  consultas: {
    title: "Consultas",
    description: "Uma visão clara de cada atendimento agendado.",
    action: "Agendar consulta",
    columns: [
      "Código / data",
      "Paciente",
      "Profissional",
      "Horário",
      "Situação",
      "",
    ],
    data: consultations,
  },
  internacoes: {
    title: "Internações",
    description:
      "Acompanhe o acolhimento, os quartos e as altas dos pacientes.",
    action: "Registrar internação",
    columns: [
      "Código / entrada",
      "Paciente",
      "Responsável",
      "Quarto",
      "Situação",
      "",
    ],
    data: admissions,
  },
};
export default function Directory({ type }) {
  const c = config[type];
  return (
    <>
      <PageHeader
        title={c.title}
        description={c.description}
        action={c.action}
        to={`/${type}/novo`}
      />
      <PreviewNote />
      <Panel
        title={`Todos os ${type === "internacoes" ? "registros de internação" : type === "consultas" ? "atendimentos" : type}`}
        subtitle={`${c.data.length} registros demonstrativos`}
      >
        <Toolbar
          placeholder={`Buscar ${type === "pacientes" ? "por nome ou CPF" : type === "profissionais" ? "por nome ou registro" : "por paciente ou código"}`}
          filters={
            type === "profissionais"
              ? ["Especialidade", "Disponibilidade"]
              : ["Situação"]
          }
        />
        <Table
          columns={c.columns}
          rows={c.data}
          renderRow={(r) => (
            <tr key={r.id}>
              {type === "pacientes" ? (
                <>
                  <td>
                    <Person {...r} secondary={r.email} />
                  </td>
                  <td>{r.cpf}</td>
                  <td>{r.phone}</td>
                  <td>{r.last}</td>
                  <td>
                    <Badge>{r.status}</Badge>
                  </td>
                </>
              ) : type === "profissionais" ? (
                <>
                  <td>
                    <Person {...r} secondary={r.email} />
                  </td>
                  <td>{r.registration}</td>
                  <td>{r.specialty}</td>
                  <td>
                    <Badge>{r.status}</Badge>
                  </td>
                </>
              ) : type === "consultas" ? (
                <>
                  <td>
                    <strong>{r.code}</strong>
                    <small className="cell-small">{dateLabel(r.date)}</small>
                  </td>
                  <td>
                    <Person {...findPatient(r.patientId)} />
                  </td>
                  <td>{findProfessional(r.professionalId).name}</td>
                  <td>{r.time}</td>
                  <td>
                    <Badge>{r.status}</Badge>
                  </td>
                </>
              ) : (
                <>
                  <td>
                    <strong>{r.code}</strong>
                    <small className="cell-small">{dateLabel(r.entry)}</small>
                  </td>
                  <td>
                    <Person {...findPatient(r.patientId)} />
                  </td>
                  <td>{findProfessional(r.professionalId).name}</td>
                  <td>
                    <span className="room-number">
                      {findRoom(r.roomId).number}
                    </span>
                  </td>
                  <td>
                    <Badge>{r.status}</Badge>
                  </td>
                </>
              )}
              <td>
                <RowLink to={`/${type}/${r.id}`} />
              </td>
            </tr>
          )}
        />
        <div className="panel-footer">
          <span>
            Exibindo {c.data.length} de {c.data.length} registros
          </span>
          <span className="pagination">
            Página <strong>1</strong> de 1
          </span>
        </div>
      </Panel>
    </>
  );
}
