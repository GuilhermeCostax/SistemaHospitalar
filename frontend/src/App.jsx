import { Route, Routes } from "react-router-dom";
import AppLayout from "./layouts/AppLayout";
import Dashboard from "./pages/Dashboard";
import Directory from "./pages/Directory";
import Rooms from "./pages/Rooms";
import Availability from "./pages/Availability";
import RecordForm from "./pages/RecordForm";
import RecordDetail from "./pages/RecordDetail";
import ClinicalAction from "./pages/ClinicalAction";
import History from "./pages/History";
import NotFound from "./pages/NotFound";
export default function App() {
  return (
    <Routes>
      <Route element={<AppLayout />}>
        <Route index element={<Dashboard />} />
        {[
          "pacientes",
          "profissionais",
          "consultas",
          "internacoes",
          "quartos",
        ].map((type) => (
          <Route key={type} path={type}>
            <Route
              index
              element={
                type === "quartos" ? <Rooms /> : <Directory type={type} />
              }
            />
            <Route path="novo" element={<RecordForm type={type} />} />
            <Route path=":id" element={<RecordDetail type={type} />} />
            <Route path=":id/editar" element={<RecordForm type={type} />} />
            {(type === "consultas"
              ? ["remarcar", "cancelar", "finalizar"]
              : type === "internacoes"
                ? ["alta", "trocar-quarto"]
                : []
            ).map((action) => (
              <Route
                key={action}
                path={`:id/${action}`}
                element={<ClinicalAction type={type} action={action} />}
              />
            ))}
          </Route>
        ))}
        <Route path="disponibilidade" element={<Availability />} />
        <Route path="historico" element={<History />} />
        <Route path="historico/:id" element={<History />} />
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}
