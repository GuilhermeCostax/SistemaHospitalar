# SistemaHospitalar

Sistema de Informação Hospitalar — trabalho de Programação Modular, 2026.

## Sprint 1 — Frontend em React

O frontend está separado em [`frontend/`](frontend/README.md), com todas as telas do sistema e dados fictícios. Esta entrega contempla apenas a interface visual, sem backend ou funcionalidades de negócio.

```bash
cd frontend
npm install
npm run dev
```

Consulte a [documentação do frontend](frontend/README.md) para ver a estrutura, as telas, os limites do protótipo e os comandos de build.



## Sprint 2

Nesta semana criei o backend do sistema com Spring Boot, na pasta `backend`. Fiz as classes do hospital (pacientes, médicos, consultas, internações, quartos, emergências e cirurgias) e as rotas da API para cadastrar e consultar cada uma delas.

Também coloquei as regras que o trabalho pediu, por exemplo:

- cada tipo de médico (clínico geral, especialista e cirurgião) tem suas próprias informações;
- cada tipo de atendimento confere suas próprias condições antes de ser feito;
- a consulta segue uma ordem de situações e não volta para uma etapa anterior (uma consulta concluída não pode voltar a ser agendada);
- o quarto não aceita mais pacientes do que a capacidade dele.

Testei as rotas pelo terminal e as regras funcionaram. Por enquanto os dados ficam só na memória, então somem quando o programa é reiniciado. Para rodar: `cd backend` e depois `mvn spring-boot:run`.

# Diagrama UML

<img width="1032" height="1122" alt="image" src="https://github.com/user-attachments/assets/656f3441-470c-4031-ac16-2581c76328fc" />

# Cartões CRC

[Cartões CRC-1.pdf](https://github.com/user-attachments/files/33115812/Cartoes.CRC-1.pdf)


