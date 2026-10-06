import { Link } from "react-router-dom";
import { BedDouble } from "lucide-react";
import { professionals, consultations, rooms } from "../data/mockData";
import { Badge, PageHeader, Panel, Person, PreviewNote, RowLink } from "../components/UI";
import "../styles/pages/Availability.css";

export default function Availability() {
  return (
    <>
      <PageHeader
        title="Disponibilidade"
        description="Visualize horários de atendimento e vagas para internação."
      />
      <PreviewNote />
      <div className="availability-grid">
        <Panel
          title="Agenda dos profissionais"
          subtitle="06 de outubro de 2026 · horários ilustrativos"
        >
          <div className="availability-date">
            <label htmlFor="agenda-date">Data de referência</label>
            <input id="agenda-date" type="date" defaultValue="2026-10-06" />
          </div>
          {professionals.map((p) => (
            <div className="availability-professional" key={p.id}>
              <div className="availability-person">
                <Person {...p} secondary={p.specialty} />
                <RowLink to={`/profissionais/${p.id}`} label="Perfil" />
              </div>
              <div className="slots">
                {["09:00", "09:30", "10:00", "10:30", "11:00", "11:30"].map(
                  (t) => {
                    const taken = consultations.some(
                      (c) =>
                        c.professionalId === p.id &&
                        c.date === "2026-10-06" &&
                        c.time === t,
                    );
                    return (
                      <span className={taken ? "taken" : "free"} key={t}>
                        {t}
                        <small>{taken ? "Agendado" : "Livre"}</small>
                      </span>
                    );
                  },
                )}
              </div>
            </div>
          ))}
          <div className="availability-foot">
            <span className="status-dot" />
            Horários livres
            <span className="status-dot muted" />
            Horários agendados
          </div>
        </Panel>
        <Panel
          title="Vagas por quarto"
          subtitle="6 leitos disponíveis para acolhimento"
        >
          <div className="vacancy-list">
            {rooms.map((r) => (
              <Link to={`/quartos/${r.id}`} key={r.id}>
                <span className="icon-box teal">
                  <BedDouble size={18} />
                </span>
                <div>
                  <strong>Quarto {r.number}</strong>
                  <small>
                    {r.floor} · Capacidade: {r.capacity}
                  </small>
                </div>
                <Badge>{r.capacity - r.occupied} livre(s)</Badge>
              </Link>
            ))}
          </div>
          <div className="panel-footer">
            Quartos ocupados estão sem vagas no exemplo.
          </div>
        </Panel>
      </div>
    </>
  );
}
