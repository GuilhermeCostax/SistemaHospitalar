import "../styles/pages/ClinicalAction.css";
import { useParams } from 'react-router-dom';
import { consultations, admissions, findPatient, findProfessional, rooms } from '../data/mockData';
import { DetailGrid, Field, FormActions, PageHeader, Panel, PreviewNote } from '../components/UI';
import NotFound from './NotFound';
const titles = { remarcar: 'Remarcar consulta', cancelar: 'Cancelar consulta', finalizar: 'Finalizar atendimento', alta: 'Registrar alta', 'trocar-quarto': 'Trocar quarto' };
export default function ClinicalAction({ type, action }) {
 const { id } = useParams(); const r = (type === 'consultas' ? consultations : admissions).find(x => x.id === id); if (!r) return <NotFound/>; const back = `/${type}/${id}`;
 return <><PageHeader title={titles[action]} description="Prévia da tela de acompanhamento do atendimento." back={back}/><PreviewNote/><Panel title={`Atendimento ${r.code}`}><DetailGrid items={[["Paciente",findPatient(r.patientId).name],["Profissional responsável",findProfessional(r.professionalId).name]]}/><form className="clinical-action" onSubmit={e => e.preventDefault()}><div className="form-grid">
 {action === 'remarcar' && <><Field label="Nova data" type="date" value={r.date}/><Field label="Novo horário" type="time" value={r.time}/><Field label="Motivo da remarcação" type="textarea" wide/></>}
 {action === 'cancelar' && <><div className="wide action-warning">Na versão funcional, o cancelamento liberará o horário do profissional e preservará o registro do atendimento.</div><Field label="Motivo do cancelamento" type="textarea" wide/></>}
 {action === 'finalizar' && <><Field label="Observações médicas" type="textarea" value={r.notes} wide/><Field label="Orientações para acompanhamento" type="textarea" required={false} wide/></>}
 {action === 'alta' && <><Field label="Data efetiva de alta" type="date"/><Field label="Profissional responsável pela alta" value={r.professionalId} options={[{value:r.professionalId,label:findProfessional(r.professionalId).name}]}/><Field label="Observações da alta" type="textarea" wide/><div className="wide action-warning">Na versão funcional, a alta encerrará a internação, liberará a vaga do quarto e manterá o atendimento no histórico.</div></>}
 {action === 'trocar-quarto' && <><Field label="Novo quarto" options={rooms.filter(room => room.id !== r.roomId && room.occupied < room.capacity).map(room => ({value:room.id,label:`Quarto ${room.number} — ${room.capacity-room.occupied} vaga(s)`}))} wide/><Field label="Motivo da transferência" type="textarea" wide/></>}
 </div><FormActions back={back} label={titles[action]}/></form></Panel></>;
}
