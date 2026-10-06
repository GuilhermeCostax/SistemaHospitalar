import "../styles/pages/Dashboard.css";
import { Link } from "react-router-dom";
import {
  ArrowRight,
  BedDouble,
  CalendarDays,
  Check,
  Clock3,
  Plus,
  Stethoscope,
  Users,
} from "lucide-react";
import {
  admissions,
  consultations,
  findPatient,
  findProfessional,
  patients,
  professionals,
  rooms,
} from "../data/mockData";
import {
  Badge,
  Metric,
  PageHeader,
  Panel,
  Person,
  RowLink,
  Table,
} from "../components/UI";
export default function Dashboard() {
  const today = consultations.filter((c) => c.date === "2026-10-06");
  const active = admissions.filter((a) => !a.discharge);
  const capacity = rooms.reduce((n, r) => n + r.capacity, 0);
  const occupied = rooms.reduce((n, r) => n + r.occupied, 0);
  return (
    <>
      <PageHeader
        eyebrow="SEU HOSPITAL, EM UM SÓ LUGAR"
        title="Visão geral"
        description="Bem-vindo, Guilherme. Acompanhe a rotina de cuidado do hospital."
        action="Agendar consulta"
        to="/consultas/novo"
      />
      <div className="date-line">
        <CalendarDays size={15} />
        Terça-feira, 06 de outubro de 2026<span>•</span>Dados de demonstração
      </div>
      <section className="welcome-banner">
        <div>
          <span className="banner-tag">
            <span />
            PAINEL DO HOSPITAL
          </span>
          <h2>
            Cuidar começa com
            <br />
            uma boa organização.
          </h2>
          <p>
            Pacientes, equipe e atendimentos conectados
            <br className="desktop-break" /> para uma rotina mais tranquila.
          </p>
          <Link to="/disponibilidade">
            Consultar disponibilidade <ArrowRight size={16} />
          </Link>
        </div>
        <div className="banner-art" aria-hidden="true">
          <div className="art-orbit orbit-one" />
          <div className="art-orbit orbit-two" />
          <div className="art-cross">
            <Plus size={100} strokeWidth={3} />
          </div>
          <span className="art-chip chip-heart">
            <Stethoscope size={22} />
          </span>
          <span className="art-chip chip-check">
            <Check size={20} /> Cuidado em primeiro lugar
          </span>
          <span className="art-dot dot-one" />
          <span className="art-dot dot-two" />
        </div>
      </section>
      <div className="metrics">
        <Metric
          label="Pacientes cadastrados"
          value={patients.length.toString().padStart(2, "0")}
          caption="Pessoas no centro do cuidado"
          icon={Users}
        />
        <Metric
          label="Consultas hoje"
          value="03"
          caption="1 atendimento em andamento"
          icon={CalendarDays}
          tone="blue"
        />
        <Metric
          label="Internações ativas"
          value={active.length.toString().padStart(2, "0")}
          caption="Acompanhamento contínuo"
          icon={BedDouble}
          tone="orange"
        />
        <Metric
          label="Profissionais"
          value={professionals.length.toString().padStart(2, "0")}
          caption="2 disponíveis para atendimento"
          icon={Stethoscope}
          tone="purple"
        />
      </div>
      <div className="dashboard-grid">
        <Panel
          title="Agenda do dia"
          subtitle="Terça-feira, 06 de outubro"
          to="/consultas"
          link="Ver agenda"
        >
          <Table
            columns={["Horário", "Paciente / profissional", "Situação", ""]}
            rows={today}
            renderRow={(c) => (
              <tr key={c.id}>
                <td>
                  <span className="time">
                    <Clock3 size={14} />
                    {c.time}
                  </span>
                </td>
                <td>
                  <Person
                    {...findPatient(c.patientId)}
                    secondary={findProfessional(c.professionalId).name}
                  />
                </td>
                <td>
                  <Badge>{c.status}</Badge>
                </td>
                <td>
                  <RowLink to={`/consultas/${c.id}`} label="Ver" />
                </td>
              </tr>
            )}
          />
          <div className="panel-footer">
            <span>{today.length} consultas previstas para hoje</span>
            <Link to="/consultas/novo">
              <Plus size={14} />
              Nova consulta
            </Link>
          </div>
        </Panel>
        <Panel
          title="Ocupação de quartos"
          subtitle="Disponibilidade dos leitos"
          to="/quartos"
          link="Ver quartos"
        >
          <div className="occupancy">
            <div className="occupancy-value">
              <strong>
                {Math.round((occupied / capacity) * 100)}
                <small>%</small>
              </strong>
              <span>de ocupação</span>
            </div>
            <div className="occupancy-track">
              <span style={{ width: `${(occupied / capacity) * 100}%` }} />
            </div>
            <div className="occupancy-legend">
              <span>
                <i />
                Ocupados <strong>{occupied}</strong>
              </span>
              <span>
                <i />
                Disponíveis <strong>{capacity - occupied}</strong>
              </span>
            </div>
            <div className="room-summary">
              {rooms.slice(0, 3).map((r) => (
                <div key={r.id}>
                  <span className="room-mini-icon">
                    <BedDouble size={16} />
                  </span>
                  <div>
                    <strong>Quarto {r.number}</strong>
                    <small>
                      {r.floor} · {r.occupied}/{r.capacity} leitos
                    </small>
                  </div>
                  <Badge>{r.status}</Badge>
                </div>
              ))}
            </div>
          </div>
        </Panel>
      </div>
      <Panel title="Acesso rápido" subtitle="O que você precisa fazer agora?">
        <div className="quick-actions">
          {[
            [
              "Cadastrar paciente",
              "Organize os dados de um novo paciente",
              "/pacientes/novo",
              Users,
            ],
            [
              "Registrar internação",
              "Prepare um novo acolhimento",
              "/internacoes/novo",
              BedDouble,
            ],
            [
              "Consultar histórico",
              "Veja a jornada de cada paciente",
              "/historico",
              ClipboardIcon,
            ],
          ].map(([title, desc, path, Icon]) => (
            <Link key={path} to={path}>
              <span className="quick-icon">
                <Icon size={22} />
              </span>
              <div>
                <strong>{title}</strong>
                <small>{desc}</small>
              </div>
              <ArrowRight size={18} />
            </Link>
          ))}
        </div>
      </Panel>
    </>
  );
}
const ClipboardIcon = CalendarDays;
