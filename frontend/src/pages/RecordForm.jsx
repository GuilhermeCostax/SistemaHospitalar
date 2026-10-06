import "../styles/pages/RecordForm.css";
import { useParams } from "react-router-dom";
import {
  patients,
  professionals,
  rooms,
  consultations,
  admissions,
} from "../data/mockData";
import {
  Field,
  FormActions,
  PageHeader,
  Panel,
  PreviewNote,
} from "../components/UI";
import NotFound from "./NotFound";
const names = {
  pacientes: "paciente",
  profissionais: "profissional",
  consultas: "consulta",
  internacoes: "internação",
  quartos: "quarto",
};
const sources = {
  pacientes: patients,
  profissionais: professionals,
  consultas: consultations,
  internacoes: admissions,
  quartos: rooms,
};
export default function RecordForm({ type }) {
  const { id } = useParams();
  const edit = Boolean(id);
  const item = edit ? sources[type].find((r) => r.id === id) : {};
  if (!item) return <NotFound />;
  const back = edit ? `/${type}/${id}` : `/${type}`;
  const options = (arr) =>
    arr.map((r) => ({
      value: r.id,
      label:
        r.name || `Quarto ${r.number} — ${r.capacity - r.occupied} vaga(s)`,
    }));
  const title = edit
    ? `Editar ${names[type]}`
    : `${type === "consultas" ? "Agendar" : type === "internacoes" ? "Registrar" : "Cadastrar"} ${names[type]}`;
  return (
    <>
      <PageHeader
        title={title}
        description="Preencha as informações para visualizar o formulário de registro."
        back={back}
      />
      <PreviewNote />
      <Panel
        title={
          type === "quartos"
            ? "Informações do quarto"
            : type === "consultas" || type === "internacoes"
              ? "Informações do atendimento"
              : "Dados pessoais e de contato"
        }
        subtitle="Os campos seguem os requisitos do Sistema Hospitalar."
      >
        <form className="record-form" onSubmit={(e) => e.preventDefault()}>
          <div className="form-grid">
            {(type === "pacientes" || type === "profissionais") && (
              <>
                <Field label="Nome completo" value={item.name} wide />
                {type === "pacientes" ? (
                  <>
                    <Field
                      label="CPF"
                      value={item.cpf}
                      hint="Formato: 000.000.000-00"
                    />
                    <Field
                      label="Data de nascimento"
                      type="date"
                      value={item.birth}
                    />
                  </>
                ) : (
                  <>
                    <Field
                      label="Registro profissional"
                      value={item.registration}
                      hint="Exemplo: CRM-MG 000001"
                    />
                    <Field
                      label="Especialidade"
                      value={item.specialty}
                      options={[
                        "Cardiologia",
                        "Clínica geral",
                        "Ortopedia",
                        "Pediatria",
                        "Enfermagem",
                      ]}
                    />
                  </>
                )}
                <Field label="Telefone" type="tel" value={item.phone} />
                <Field
                  label="E-mail para contato"
                  type="email"
                  value={item.email}
                />
                {type === "pacientes" && (
                  <Field label="Endereço completo" value={item.address} wide />
                )}
              </>
            )}
            {(type === "consultas" || type === "internacoes") && (
              <>
                <Field
                  label="Paciente"
                  value={item.patientId}
                  options={options(patients)}
                />
                <Field
                  label="Profissional responsável"
                  value={item.professionalId}
                  options={options(professionals)}
                />
                {type === "consultas" ? (
                  <>
                    <Field
                      label="Data da consulta"
                      type="date"
                      value={item.date}
                    />
                    <Field
                      label="Horário"
                      type="time"
                      value={item.time}
                      hint="A disponibilidade será validada nas próximas etapas."
                    />
                    <Field
                      label="Motivo da consulta"
                      value={item.reason}
                      wide
                    />
                    <Field
                      label="Observações médicas"
                      type="textarea"
                      value={item.notes}
                      wide
                      required={false}
                    />
                  </>
                ) : (
                  <>
                    <Field
                      label="Quarto"
                      options={options(rooms)}
                      value={item.roomId}
                      hint="A ocupação e a capacidade serão validadas nas próximas etapas."
                    />
                    <Field
                      label="Data de entrada"
                      type="date"
                      value={item.entry}
                    />
                    <Field
                      label="Data prevista de alta"
                      type="date"
                      value={item.expected}
                    />
                    <Field
                      label="Data efetiva de alta"
                      type="date"
                      value={item.discharge}
                      required={false}
                    />
                    <Field
                      label="Observações"
                      type="textarea"
                      value={item.notes}
                      wide
                      required={false}
                    />
                  </>
                )}
              </>
            )}
            {type === "quartos" && (
              <>
                <Field label="Número de identificação" value={item.number} />
                <Field label="Andar" value={item.floor} />
                <Field
                  label="Capacidade máxima de pacientes"
                  type="number"
                  value={item.capacity}
                />
                <Field
                  label="Situação atual"
                  value={item.status}
                  options={["Disponível", "Ocupado"]}
                />
              </>
            )}
          </div>
          <FormActions
            back={back}
            label={
              edit
                ? "Salvar alterações"
                : type === "consultas"
                  ? "Agendar consulta"
                  : type === "internacoes"
                    ? "Registrar internação"
                    : "Salvar cadastro"
            }
          />
        </form>
      </Panel>
    </>
  );
}
