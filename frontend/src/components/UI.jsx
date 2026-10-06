import "../styles/components/UI.css";
import "../styles/components/Panel.css";
import "../styles/components/Table.css";
import "../styles/components/Person.css";
import "../styles/components/Toolbar.css";
import "../styles/components/Form.css";
import "../styles/components/DetailGrid.css";
import { Link } from "react-router-dom";
import {
  ArrowLeft,
  ArrowUpRight,
  ChevronRight,
  Info,
  Plus,
  Search,
} from "lucide-react";
export function PageHeader({
  eyebrow = "GESTÃO HOSPITALAR",
  title,
  description,
  action,
  to,
  back,
}) {
  return (
    <div className="page-header">
      <div>
        {back && (
          <Link className="back" to={back}>
            <ArrowLeft size={15} /> Voltar
          </Link>
        )}
        <p className="eyebrow">{eyebrow}</p>
        <h1>{title}</h1>
        <p className="description">{description}</p>
      </div>
      {action && (
        <Link className="button primary" to={to}>
          <Plus size={17} />
          {action}
        </Link>
      )}
    </div>
  );
}
export function Badge({ children }) {
  const tone = [
    "Disponível",
    "Concluída",
    "Alta realizada",
    "Confirmada",
  ].includes(children)
    ? "green"
    : ["Internado", "Em atendimento", "Em andamento", "Ocupado"].includes(
          children,
        )
      ? "amber"
      : children === "Cancelada"
        ? "gray"
        : "blue";
  return (
    <span className={`badge ${tone}`}>
      <span />
      {children}
    </span>
  );
}
export function Avatar({ name, initials, small }) {
  return (
    <span className={`avatar ${small ? "small" : ""}`} aria-label={name}>
      {initials ||
        name
          ?.split(" ")
          .filter((x) => !["Dra.", "Dr."].includes(x))
          .map((x) => x[0])
          .slice(0, 2)
          .join("")}
    </span>
  );
}
export function Person({ name, initials, secondary }) {
  return (
    <div className="person">
      <Avatar name={name} initials={initials} small />
      <div>
        <strong>{name}</strong>
        {secondary && <small>{secondary}</small>}
      </div>
    </div>
  );
}
export function Panel({ title, subtitle, children, to, link = "Ver todos" }) {
  return (
    <section className="panel">
      <div className="panel-heading">
        <div>
          <h2>{title}</h2>
          {subtitle && <p>{subtitle}</p>}
        </div>
        {to && (
          <Link className="text-link" to={to}>
            {link}
            <ArrowUpRight size={15} />
          </Link>
        )}
      </div>
      {children}
    </section>
  );
}
export function Metric({ label, value, caption, icon: Icon, tone = "teal" }) {
  return (
    <div className="metric">
      <div className="metric-top">
        <span>{label}</span>
        <span className={`icon-box ${tone}`}>
          <Icon size={20} />
        </span>
      </div>
      <strong>{value}</strong>
      <small>{caption}</small>
    </div>
  );
}
export function Table({ columns, rows, renderRow }) {
  return (
    <div className="table-scroll">
      <table>
        <thead>
          <tr>
            {columns.map((c) => (
              <th key={c}>{c}</th>
            ))}
          </tr>
        </thead>
        <tbody>{rows.map(renderRow)}</tbody>
      </table>
    </div>
  );
}
export function RowLink({ to, label = "Detalhes" }) {
  return (
    <Link className="row-link" to={to}>
      {label}
      <ChevronRight size={15} />
    </Link>
  );
}
export function PreviewNote() {
  return (
    <div className="preview-note">
      <Info size={16} />
      <span>
        Protótipo da Sprint 1. Dados fictícios; ações de cadastro e atendimento
        são apenas demonstrativas.
      </span>
    </div>
  );
}
export function Toolbar({ placeholder, filters = [] }) {
  return (
    <div className="toolbar">
      <label className="search">
        <Search size={18} />
        <input aria-label={placeholder} placeholder={placeholder} />
      </label>
      <div className="filters">
        {filters.map((f) => (
          <select key={f} aria-label={f} defaultValue="">
            <option value="">{f}</option>
            <option>Todos</option>
          </select>
        ))}
      </div>
    </div>
  );
}
export function Field({
  label,
  type = "text",
  value,
  options,
  wide,
  hint,
  required = true,
}) {
  const id = label.toLowerCase().replace(/[^a-z0-9]/g, "-");
  return (
    <div className={`field ${wide ? "wide" : ""}`}>
      <label htmlFor={id}>
        {label}
        {required && <span className="required"> *</span>}
      </label>
      {options ? (
        <select id={id} defaultValue={value || ""}>
          <option value="">Selecione uma opção</option>
          {options.map((o) => (
            <option
              key={typeof o === "string" ? o : o.value}
              value={typeof o === "string" ? o : o.value}
            >
              {typeof o === "string" ? o : o.label}
            </option>
          ))}
        </select>
      ) : type === "textarea" ? (
        <textarea
          id={id}
          defaultValue={value}
          placeholder="Escreva as informações aqui…"
          rows={4}
        />
      ) : (
        <input id={id} type={type} defaultValue={value} placeholder={label} />
      )}{" "}
      {hint && <small>{hint}</small>}
    </div>
  );
}
export function FormActions({ back, label = "Salvar cadastro" }) {
  return (
    <div className="form-actions">
      <span>* Campos obrigatórios na versão final</span>
      <div>
        <Link className="button secondary" to={back}>
          Voltar
        </Link>
        <button
          className="button primary"
          type="button"
          disabled
          title="Disponível em uma próxima etapa do projeto"
        >
          {label}
        </button>
      </div>
    </div>
  );
}
export function DetailGrid({ items }) {
  return (
    <dl className="detail-grid">
      {items.map(([label, value]) => (
        <div key={label}>
          <dt>{label}</dt>
          <dd>{value ?? "Não informado"}</dd>
        </div>
      ))}
    </dl>
  );
}
