# Testes de Performance

Testes de carga com k6 executado em container. Os cenários medem latência, throughput, falhas e percentis no registro de votos e na consulta da apuração. O frontend não precisa estar em execução.

## Ambiente isolado

O Docker Compose sobe dois containers PostgreSQL independentes:

| Uso | Serviço | Database | Porta | Volume |
| --- | --- | --- | --- | --- |
| Desenvolvimento normal | `postgres` | `voting_db` | `5432` | `voting-data` |
| Testes de performance | `postgres-performance` | `voting_perf_db` | `5433` | `voting-performance-data` |

O backend normal usa `voting_db`. Com o profile `perf`, usa exclusivamente `voting_perf_db`, aplica as mesmas migrations do Flyway e considera todo CPF válido apto a votar. Os dados do k6 permanecem no volume de performance para conferência até a limpeza manual.

## Execução

São necessários apenas PostgreSQL, backend e k6. Na raiz, suba o banco:

```bash
docker compose up -d
```

Inicie o backend com o profile `perf`.

Linux/WSL:

```bash
cd backend
SPRING_PROFILES_ACTIVE=perf ./mvnw spring-boot:run
```

PowerShell:

```powershell
cd backend
$env:SPRING_PROFILES_ACTIVE="perf"
./mvnw spring-boot:run
```

### Carga de votos

O setup identifica um associado com CPF válido por voto, cria uma pauta e abre uma sessão de 30 minutos. A carga alterna `YES` e `NO`, sem reutilizar associado na mesma sessão. As métricas e thresholds principais filtram apenas requisições com a tag `endpoint:vote`; o tempo do setup não interfere no percentil do endpoint de voto.

```bash
docker compose --profile performance run --rm \
  -e TOTAL_VOTES=10 \
  -e VUS=2 \
  k6 run /scripts/scripts/vote-load-test.js
```

| Variável | Padrão |
| --- | --- |
| `BASE_URL` | `http://host.docker.internal:8080/api/v1` via Compose |
| `TOTAL_VOTES` | `1000` |
| `VUS` | `100` |

## Verificação dos dados gerados

Os dados criados pelo k6 permanecem no banco isolado `voting_perf_db` enquanto o volume de performance existir. Para acessar o PostgreSQL de performance:

```bash
docker exec -it voting-postgres-performance psql -U postgres -d voting_perf_db
```

Dentro do `psql`, consulte os registros gerados:

```sql
SELECT COUNT(*) FROM associate;
SELECT COUNT(*) FROM proposal;
SELECT COUNT(*) FROM voting_session;
SELECT COUNT(*) FROM vote;
```

Após um teste com `TOTAL_VOTES=10`, devem existir os dez associados e votos criados pelo cenário, além da pauta e da sessão utilizadas.

O script informa no terminal o ID da sessão criada:

```text
Voting session created for result validation: 1
```

Use o ID exibido para consultar a apuração enquanto o backend estiver em execução:

```bash
curl http://localhost:8080/api/v1/voting-sessions/<ID_DA_SESSAO>/result
```

Como o script alterna votos `YES` e `NO`, dez votos aceitos produzem, por exemplo:

```json
{
  "votingSessionId": 1,
  "yesVotes": 5,
  "noVotes": 5,
  "totalVotes": 10
}
```

O ID `1` é apenas um exemplo e deve ser substituído pelo valor informado pelo k6.

### Carga da apuração

`SESSION_ID` é obrigatório.

```bash
docker compose --profile performance run --rm \
  -e SESSION_ID=<ID_DA_SESSAO> \
  -e VUS=50 \
  -e DURATION=30s \
  k6 run /scripts/scripts/result-load-test.js
```

| Variável | Padrão |
| --- | --- |
| `BASE_URL` | `http://host.docker.internal:8080/api/v1` via Compose |
| `SESSION_ID` | obrigatório |
| `VUS` | `50` |
| `DURATION` | `30s` |

Os thresholds exigem mais de 99% dos checks aprovados, menos de 1% de falhas HTTP e p95 abaixo de 1 segundo para votos e 500 ms para apuração. Os resultados dependem do hardware local.

## Limpeza manual

Para remover somente os dados de performance, pare e remova o container correspondente e depois exclua seu volume:

```bash
docker compose stop postgres-performance
docker compose rm -f postgres-performance
docker volume rm desafio-votacao-fullstack_voting-performance-data
docker compose up -d postgres-performance
```

O container e o volume de desenvolvimento não são alterados. Na próxima inicialização do backend com o profile `perf`, o Flyway recria o schema no banco vazio.
