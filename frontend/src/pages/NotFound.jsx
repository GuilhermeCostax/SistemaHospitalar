import "../styles/pages/NotFound.css";
import { Link } from "react-router-dom";
import { SearchX } from "lucide-react";
export default function NotFound() {
  return (
    <div className="empty-state">
      <SearchX size={42} />
      <span>404</span>
      <h1>Registro não encontrado</h1>
      <p>
        A página ou o registro solicitado não está disponível neste protótipo.
      </p>
      <Link className="button primary" to="/">
        Voltar à visão geral
      </Link>
    </div>
  );
}
