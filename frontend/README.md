# HSoft — Frontend

Interface em React para a **parte 1 da Sprint 1** do Sistema Hospitalar: telas sem funcionalidade de negócio. Os registros são fictícios. Não há API, backend, autenticação, banco de dados, persistência, validação de conflitos ou alteração de registros.

## Executar

Requisito: Node.js 20.19+ ou 22.12+ com npm.

```bash
cd frontend
npm install
npm run dev
```

Abra o endereço mostrado pelo Vite, normalmente `http://localhost:5173`.

```bash
npm run build
npm run preview
```

O build é gerado em `dist/`. A navegação utiliza `HashRouter`, permitindo servir os arquivos em uma hospedagem estática sem configurar redirecionamentos para as rotas. O caminho base do Vite é `/`; ajuste `base` em `vite.config.js` caso publique em um subdiretório.

## Organização

```text
frontend/
├── public/                 # Recursos estáticos
├── src/
│   ├── components/         # Elementos reutilizáveis de interface
│   ├── data/               # Dados fictícios e formatadores
│   ├── layouts/            # Estrutura principal da aplicação
│   ├── pages/              # Componentes JSX das telas
│   ├── styles/             # Todos os estilos da aplicação
│   │   ├── components/     # CSS dos componentes compartilhados
│   │   ├── layouts/        # CSS da estrutura principal
│   │   ├── pages/          # CSS específico de cada página
│   │   └── global.css      # Fontes, reset, variáveis e acessibilidade
│   ├── App.jsx             # Rotas
│   └── main.jsx            # Entrada da aplicação
├── index.html
├── package.json
└── vite.config.js
```

## Telas entregues

| Área | Telas e informações |
| --- | --- |
| Visão geral | Indicadores, agenda do dia, ocupação e acesso rápido |
| Pacientes | Listagem, cadastro, edição e detalhes; nome, CPF, nascimento, telefone, endereço e e-mail |
| Profissionais | Listagem, cadastro, edição e detalhes; nome, registro profissional, especialidade, telefone e e-mail |
| Consultas | Listagem, agendamento, edição, detalhes, remarcação, cancelamento e finalização; paciente, profissional, data, horário, motivo e observações médicas |
| Internações | Listagem, registro, edição, detalhes, troca de quarto e alta; paciente, profissional, quarto, entrada, previsão de alta, alta efetiva e observações |
| Quartos | Visão dos quartos, cadastro, edição e detalhes; número, andar, capacidade, ocupação e situação |
| Disponibilidade | Agenda ilustrativa por profissional e vagas por quarto |
| Histórico médico | Seleção de paciente e linha do tempo de consultas, internações e observações |
| Registro inexistente | Tela de retorno para URLs ou IDs inválidos |

## Limites desta entrega

- Links e menu permitem navegar e abrir todas as telas; o menu móvel pode ser aberto e fechado.
- Campos permitem visualizar e preencher formulários, sem salvar ou validar os dados.
- Busca, filtros e data de referência são elementos visuais; não filtram os exemplos.
- Botões de salvar, excluir, finalizar, cancelar ou registrar alta ficam desabilitados.
- Datas, indicadores e horários são fixos e ilustrativos, não representam informações em tempo real.
- O histórico preserva a associação entre os exemplos de pacientes e atendimentos.
- A interface exibe as condições de disponibilidade, capacidade e horários. Aplicar essas regras pertence às próximas etapas.
- As fontes são carregadas do Google Fonts; a interface utiliza fontes de fallback quando estiver offline.

## Referências

As telas seguem o PDF de orientações de Programação Modular de 2026, o diagrama UML e os cartões CRC vinculados no README principal. A arquitetura em camadas, a API Spring Boot, a persistência e os testes de regras de negócio mencionados no trabalho ficam fora desta entrega de frontend.

## Organização do CSS

Todos os arquivos CSS ficam em `src/styles/`. Cada página importa seu próprio arquivo de `styles/pages/`, incluindo as suas regras responsivas. Os estilos do menu, cabeçalho e estrutura principal ficam em `styles/layouts/AppLayout.css`. Os componentes compartilhados possuem estilos em `styles/components/` (painéis, tabelas, pessoas, filtros e formulários). `styles/global.css` contém apenas a base geral da aplicação: fontes, variáveis, reset, tipografia básica, foco e redução de movimento. As listagens de pacientes, profissionais, consultas e internações reutilizam a página `Directory` e seu CSS.
