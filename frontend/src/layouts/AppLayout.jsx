import "../styles/layouts/AppLayout.css";
import { useEffect, useState } from "react";
import { Link, NavLink, Outlet, useLocation } from "react-router-dom";
import {
  Activity,
  BedDouble,
  CalendarDays,
  ChevronDown,
  CircleHelp,
  ClipboardList,
  HeartPulse,
  LayoutDashboard,
  Menu,
  Stethoscope,
  Users,
  X,
} from "lucide-react";
const navigation = [
  ["/", "Visão geral", LayoutDashboard],
  ["/pacientes", "Pacientes", Users],
  ["/profissionais", "Profissionais", Stethoscope],
  ["/consultas", "Consultas", CalendarDays],
  ["/internacoes", "Internações", BedDouble],
  ["/quartos", "Quartos", ClipboardList],
  ["/disponibilidade", "Disponibilidade", Activity],
  ["/historico", "Histórico médico", HeartPulse],
];
export default function AppLayout() {
  const [open, setOpen] = useState(false);
  const location = useLocation();
  useEffect(() => {
    setOpen(false);
    window.scrollTo(0, 0);
  }, [location.pathname]);
  const current = navigation.find(([path]) =>
    path === "/"
      ? location.pathname === "/"
      : location.pathname.startsWith(path),
  );
  return (
    <div className="app-shell">
      {open && (
        <button
          className="sidebar-overlay"
          aria-label="Fechar menu"
          onClick={() => setOpen(false)}
        />
      )}
      <aside className={`sidebar ${open ? "open" : ""}`}>
        <Link to="/" className="brand">
          <span className="brand-icon">
            <HeartPulse size={26} />
          </span>
          <span>
            HSoft<small>SISTEMA HOSPITALAR</small>
          </span>
        </Link>
        <button
          className="mobile-close"
          aria-label="Fechar menu"
          onClick={() => setOpen(false)}
        >
          <X />
        </button>
        <div className="workspace">
          <span className="hospital-mark">H</span>
          <div>
            <strong>Hospital Horizonte</strong>
            <small>Unidade Belo Horizonte</small>
          </div>
          <ChevronDown size={14} />
        </div>
        <p className="nav-label">PRINCIPAL</p>
        <nav aria-label="Menu principal">
          {navigation.map(([path, label, Icon]) => (
            <NavLink key={path} to={path} end={path === "/"}>
              <Icon size={19} />
              {label}
              {path === "/consultas" && <span className="nav-count">3</span>}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-bottom">
          <div className="help-card">
            <CircleHelp size={19} />
            <strong>Uma gestão mais humana.</strong>
            <p>Organização e cuidado em cada atendimento.</p>
          </div>
          <div className="sidebar-version">
            <span className="status-dot" />
            Sprint 1 · Interface visual<small>HSoft © 2026</small>
          </div>
        </div>
      </aside>
      <div className="app-body">
        <header className="topbar">
          <div className="breadcrumb">
            <button
              className="menu-toggle"
              aria-label="Abrir menu"
              onClick={() => setOpen(true)}
            >
              <Menu size={22} />
            </button>
            <span>Workspace</span>
            <span className="breadcrumb-slash">/</span>
            <strong>{current?.[1] || "Página"}</strong>
          </div>
          <div className="topbar-right">
            <span className="demo-label">Ambiente demonstrativo</span>
            <span className="topbar-divider" />
            <span className="profile-avatar">GC</span>
            <div className="profile">
              <strong>Guilherme Costa</strong>
              <small>Administrador</small>
            </div>
          </div>
        </header>
        <main>
          <Outlet />
        </main>
        <footer className="footer">
          <span>HSoft · Sistema de Informação Hospitalar</span>
          <span>Programação Modular · 2026</span>
        </footer>
      </div>
    </div>
  );
}
