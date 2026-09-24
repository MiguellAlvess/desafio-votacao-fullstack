# Desafio Votação

API REST para cadastrar pautas, abrir sessões de votação, registrar votos de associados e consultar a apuração. Os dados são persistidos em PostgreSQL. A autenticação foi abstraída conforme o desafio.

## Tecnologias

-
- Java 21, Spring Boot 4.1.1, Spring Data JPA, PostgreSQL 17, Flyway, springdoc-openapi e Maven Wrapper.
- Spring Boot 4.1.1
- Spring Data JPA
- PostgreSQL 17
- Flyway
- springdoc-openapi
- Maven Wrapper
- JUnit 5
- Mockito
- MockMvc
- Testcontainers
- Docker e Docker Compose

## Arquitetura

Monólito modular organizado por funcionalidade (_package by feature_). Os módulos principais separam controller, service, repository, domain e DTOs. `external/eligibility` simula a consulta de elegibilidade; `core` concentra o tratamento de erros; `config` reúne OpenAPI e CORS.

```text
backend/src/main/java/br/com/miguelalves/voting/
├── associate/
├── proposal/
├── votingsession/
├── vote/
├── external/
├── core/
└── config/
```

## Funcionalidades

- Cadastro e identificação de associados por CPF.
- Cadastro e listagem de pautas.
- Abertura de sessão com duração configurável ou padrão de um minuto; listagem de sessões abertas.
- Voto `YES`/`NO` durante a sessão, com bloqueio de duplicidade e elegibilidade simulada.
- Apuração de votos, API versionada e documentação Swagger.

## API

| Método | Endpoint                                           | Descrição                             |
| ------ | -------------------------------------------------- | ------------------------------------- |
| POST   | `/api/v1/associates`                               | Cadastra associado                    |
| POST   | `/api/v1/associates/identify`                      | Identifica ou cria associado pelo CPF |
| POST   | `/api/v1/proposals`                                | Cadastra pauta                        |
| GET    | `/api/v1/proposals`                                | Lista pautas                          |
| GET    | `/api/v1/proposals/management`                     | Lista pautas para gestão              |
| POST   | `/api/v1/proposals/{proposalId}/sessions`          | Abre sessão para a pauta              |
| GET    | `/api/v1/voting-sessions/open`                     | Lista sessões abertas                 |
| GET    | `/api/v1/voting-sessions/{id}`                     | Consulta sessão por ID                |
| POST   | `/api/v1/voting-sessions/{votingSessionId}/votes`  | Registra voto                         |
| GET    | `/api/v1/voting-sessions/{votingSessionId}/result` | Consulta a apuração                   |

Exemplos de requisição:

```http
POST /api/v1/associates/identify
Content-Type: application/json

{"cpf":"52998224725"}
```

```http
POST /api/v1/voting-sessions/1/votes
Content-Type: application/json

{"associateId":1,"choice":"YES"}
```

O CPF do exemplo é válido, mas a elegibilidade para votar é aleatória. O voto informa o ID do associado, não o CPF.

## Fluxo principal

1. O associado informa o CPF, a API identifica ou cria o cadastro.
2. O cliente consulta as sessões abertas e escolhe uma delas.
3. Ao votar, a API verifica sessão, associado, duplicidade e elegibilidade.
4. O resultado fica disponível pelo endpoint de apuração.

## Tarefas bônus

| Bônus                   | Implementação                                                                             |
| ----------------------- | ----------------------------------------------------------------------------------------- |
| Integração externa fake | `AssociateEligibilityClient` e `FakeAssociateEligibilityClient`; sem chamada HTTP externa |
| Performance             | k6 com banco isolado `voting_perf_db`; [detalhes](../performance/README.md)                 |
| Versionamento           | Prefixo `/api/v1`                                                                         |

## Execução local

Pré-requisitos: Java 21 e Docker com Docker Compose. As portas `5432` e `8080` devem estar livres. Não é necessário instalar Maven.

Na raiz do repositório, suba o banco:

```bash
docker compose up -d
```

O Compose configura PostgreSQL em `localhost:5432`, banco `voting_db`, usuário `postgres` e senha `postgres` para desenvolvimento local. Em outro terminal, ainda na raiz:

```bash
cd backend
./mvnw spring-boot:run
```

Verifique a API em `http://localhost:8080/api/v1/proposals`. A documentação interativa está em `http://localhost:8080/swagger-ui/index.html` e o OpenAPI JSON em `http://localhost:8080/v3/api-docs`.

Para executar os testes, com Docker ativo:

```bash
cd backend
./mvnw test
```

Os testes de repository usam PostgreSQL via Testcontainers. Há também testes unitários de domínio e service e testes web com MockMvc.

O CORS permite que o frontend local em `http://localhost:5173` faça requisições GET e POST para `/api/**`.
