# Desafio Votação - Frontend

Interface web para identificação de associados, participação em votações e consulta de resultados. Também oferece uma área de gestão para cadastrar pautas, abrir sessões e acompanhar seus estados.

## Tecnologias

- React 19 e TypeScript
- Vite
- React Router
- TanStack Query
- Axios
- React Hook Form e Zod
- shadcn/ui, Base UI e Tailwind CSS
- Lucide React, Sonner e date-fns
- Vitest e React Testing Library

## Arquitetura

O código é organizado por funcionalidade (*package by feature*). Pages compõem as telas; cada domínio agrupa componentes, hooks, services, schemas, tipos e utilitários. `components/ui` contém a base visual e `lib` concentra a configuração HTTP e o armazenamento local da identificação.

```text
src/
├── app/
├── associate/
├── proposal/
├── voting-session/
├── vote/
├── pages/
├── components/
├── lib/
└── tests/
```

## Funcionalidades

- Identificação e validação de associado por CPF, mantida em `sessionStorage`.
- Listagem de sessões abertas e registro de voto `YES` ou `NO` com confirmação.
- Feedback de erros de votação e elegibilidade.
- Consulta do resultado de uma sessão.
- Cadastro e busca local de pautas por título.
- Abertura de sessões e visualização de seus estados.
- Listagem de sessões em andamento.

## Rotas

| Rota | Descrição |
| --- | --- |
| `/` | Identificação do associado |
| `/votacoes` | Sessões abertas |
| `/votacoes/:votingSessionId` | Registro de voto |
| `/votacoes/:votingSessionId/sucesso` | Confirmação do voto |
| `/votacoes/:votingSessionId/resultado` | Resultado da votação |
| `/gestao` | Gestão de pautas e sessões |

## Integração com a API

O frontend usa `VITE_API_URL` como endereço-base da API. Sem essa variável, utiliza `http://localhost:8080/api/v1`.

Crie o arquivo local a partir do exemplo:

```bash
cp .env.example .env
```

```env
VITE_API_URL=http://localhost:8080/api/v1
```

## Fluxo principal

Participante:

1. Informa o CPF.
2. Escolhe uma sessão aberta.
3. Seleciona e confirma um voto.
4. Consulta o resultado.

Gestão:

1. Cadastra uma pauta.
2. Abre uma sessão.
3. Acompanha o status das sessões.
4. Consulta o resultado.

## Execução local

Pré-requisitos: Node.js e npm. O backend deve estar disponível em `http://localhost:8080` para o fluxo integrado.

```bash
cd frontend
npm install
npm run dev
```

A aplicação fica disponível em `http://localhost:5173` por padrão.

## Testes e build

```bash
npm run test:run
npm run lint
npm run build
```

Os testes usam Vitest, React Testing Library e ambiente jsdom.
