import { BedDouble } from "lucide-react";
import { rooms } from "../data/mockData";
import { Badge, PageHeader, PreviewNote, RowLink, Toolbar } from "../components/UI";
import "../styles/pages/Rooms.css";

export default function Rooms() {
  return (
    <>
      <PageHeader
        title="Quartos hospitalares"
        description="Cada espaço preparado para acolher e cuidar."
        action="Cadastrar quarto"
        to="/quartos/novo"
      />
      <PreviewNote />
      <div className="summary-strip">
        <span>
          <strong>06</strong> quartos cadastrados
        </span>
        <span>
          <strong>10</strong> leitos no total
        </span>
        <span>
          <strong>06</strong> leitos disponíveis
        </span>
        <span>
          <strong>40%</strong> de ocupação
        </span>
      </div>
      <Toolbar
        placeholder="Buscar pelo número do quarto"
        filters={["Andar", "Situação"]}
      />
      <div className="room-grid">
        {rooms.map((r) => (
          <section className="room-card" key={r.id}>
            <div className="room-card-top">
              <span className="icon-box teal">
                <BedDouble size={23} />
              </span>
              <Badge>{r.status}</Badge>
            </div>
            <h2>Quarto {r.number}</h2>
            <p>
              {r.floor} <span>·</span> {r.type}
            </p>
            <div
              className="bed-icons"
              aria-label={`${r.occupied} leitos ocupados de ${r.capacity}`}
            >
              {Array.from({ length: r.capacity }, (_, i) => (
                <span key={i} className={i < r.occupied ? "occupied" : ""}>
                  <BedDouble size={26} />
                  <small>{i < r.occupied ? "Ocupado" : "Livre"}</small>
                </span>
              ))}
            </div>
            <div className="room-card-bottom">
              <span>
                {r.occupied} de {r.capacity} leitos ocupados
              </span>
              <RowLink to={`/quartos/${r.id}`} />
            </div>
          </section>
        ))}
      </div>
    </>
  );
}
