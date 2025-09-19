# GigCred Runbooks

This directory is reserved for operational runbooks and SLO documentation. Populate with environment-specific procedures such as:

- **KYC pipeline recovery** – steps for reprocessing failed Flutterwave webhook callbacks.
- **Ledger reconciliation** – how to execute `POST /api/v1/ledger/reconcile`, inspect mismatches and perform adjustments.
- **Outbox replay** – using `/api/v1/ops/outbox` endpoints or Kafka tooling to replay stuck events.
- **Incident communication** – escalation matrix, status page updates and RCA template.

Add SLO definitions alongside alerts (e.g., 99.5% success for remittance transfers, p95 < 3s) and map them to dashboards once Grafana/Prometheus are wired.
