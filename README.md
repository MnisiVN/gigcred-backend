# GigCred Backend

GigCred is a wallet, debit card and credit-builder platform purpose-built for gig and informal workers in South Africa. This repository contains a production-ready scaffold for a modular Spring Boot 3.3 monorepo targeting Java 21 with a hexagonal architecture, resilience patterns and cloud-native deployment assets.

## Monorepo layout

| Module | Description |
| --- | --- |
| `common/` | Shared domain and data adapters (idempotency, events). |
| `integrations/flutterwave-adapter` | Provider adapter using OpenFeign with Resilience4j. |
| `services/*-service` | Domain services for gateway, onboarding, accounts, payments, ledger, scoring, loans, compliance, notifications and ops. |
| `docs/` | OpenAPI specification, sequence and C4 diagrams, runbooks. |
| `infrastructure/helm` | Helm chart for deploying all services with configurable replicas and HPAs. |
| `infrastructure/terraform` | Terraform stubs for VPC/KMS and future GitOps automation. |
| `docker-compose.yml` | Local dependencies (PostgreSQL, Kafka, Redis, Zookeeper). |

Each service exposes a SpringDoc-generated OpenAPI endpoint (`/v3/api-docs` and `/swagger-ui`) and follows the ports-and-adapters style with application, domain and adapter packages.

## Getting started

1. **Prerequisites**
   - Java 21
   - Docker + Docker Compose
   - Gradle 8.8+ (wrapper included)

2. **Bootstrap infrastructure**
   ```bash
   docker-compose up -d
   ```

3. **Run the full test suite**
   ```bash
   ./gradlew clean test
   ```

4. **Run a service** (example: payments)
   ```bash
   ./gradlew :services:payments-service:bootRun
   ```

   Services load configuration from `application.yml`. Production secrets should be provided through environment variables or a secrets manager (see Helm values for placeholders).

## Key capabilities

- **Hexagonal design:** clear separation between REST controllers, application services, domain logic and adapters.
- **Resilience:** idempotency middleware, retry/circuit breaker defaults for the Flutterwave integration, transactional outbox scaffolding and saga-ready orchestration hooks.
- **Security:** OAuth2 resource servers with JWT scopes (e.g., `wallet:read`, `remit:write`), structured logging and data minimisation (no PAN storage, placeholder secrets).
- **Observability:** Actuator probes, Micrometer metrics, OpenTelemetry ready instrumentation hooks and structured JSON logging.
- **Asynchronous foundations:** Kafka-compatible outbox/inbox pattern placeholders, Redis cache/flag support and Testcontainers coverage in unit/integration tests.
- **Infrastructure as Code:** reusable Helm chart, Terraform VPC/KMS module and Docker Compose for local parity.

## OpenAPI specification

The canonical OpenAPI definition for all public endpoints lives at [`docs/openapi/gigcred-api.yaml`](docs/openapi/gigcred-api.yaml). Each service automatically hosts generated docs at runtime. CI should validate drift by comparing generated specs with the static file.

## Diagrams

Mermaid diagrams capturing the context, components and key flows:

- [C4 Context](docs/diagrams/c4-context.mmd)
- [Payments Component Diagram](docs/diagrams/c4-component.mmd)
- [Remittance Transfer Sequence](docs/diagrams/sequence-remittance.mmd)
- [Webhook Settlement Sequence](docs/diagrams/sequence-webhook-settle.mmd)
- [Loan Auto-Debit Sequence](docs/diagrams/sequence-loan-auto-debit.mmd)

Render them via the Mermaid CLI or GitHub-supported Markdown viewer.

## Runbooks & SLOs

See `docs/runbooks/` for operational guidance (placeholders) and expand with environment-specific steps, SLO budgets and alerting policies once metrics backends are wired.

## Deployment

- Use the provided Helm chart with GitOps (e.g., Argo CD) to deploy microservices. Configure ingress, autoscaling and secrets via `values.yaml`.
- Terraform stubs bootstrap AWS primitives (VPC, KMS). Extend with RDS, MSK/managed Kafka, Redis and observability stacks per environment.
- Enable blue/green or canary rollouts via Helm by integrating with Argo Rollouts or Flagger.

## Testing strategy

- **Unit & Integration Tests:** Gradle modules include JUnit 5 tests with Mockito, WireMock and Testcontainers where appropriate (idempotency, webhook ordering, ledger reconciliation, payout retries).
- **Contract tests:** the Flutterwave adapter uses WireMock to validate Feign clients and resilience behaviour.
- **Coverage:** JaCoCo is configured globally; extend thresholds as implementation matures.

## Next steps

- Implement persistence layers (JPA repositories) per service.
- Integrate Kafka producers/consumers for the outbox/inbox pattern.
- Harden security by integrating Vault or AWS Secrets Manager.
- Build CI pipelines to run linting, OpenAPI drift checks and infrastructure validation.

