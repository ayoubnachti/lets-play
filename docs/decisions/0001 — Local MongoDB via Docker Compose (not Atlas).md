## Context
The project needs a MongoDB instance for local development. Two realistic options: a containerized instance managed via Docker Compose (already the local-dev pattern used elsewhere in this workflow), or a MongoDB Atlas free-tier cluster. CI/integration tests use Test-containers regardless of this choice, so this decision is scoped to *manual local dev only* — it doesn't affect how tests run.

## Decision
Use Docker Compose to run MongoDB locally for day-to-day development. Atlas is noted as a considered alternative, not adopted for v1.

## Alternatives considered
- **MongoDB Atlas free tier** — rejected for now: introduces a network dependency for local dev (no offline work), requires managing a cloud credential/connection string per contributor instead of a single shared `compose.yaml`, and adds account setup friction that doesn't pay for itself on a single-developer training project. Worth revisiting if this project ever needs to be reachable by more than one person's machine (e.g. a demo environment) without spinning up infra.

## Consequences
- Positive: zero external dependency to start developing — `docker compose up` and you're connected; matches the existing Compose pattern from other projects; consistent with how CI/Test-containers already treats Mongo as ephemeral and disposable. 
- Negative: doesn't exercise "connect to a real remote cluster" as a skill — if that's ever a gap worth closing, it should be a deliberate follow-up, not implicit. 
- Follow-up: connection string must still be externalized (env var / `.env`), not hardcoded, even though it's "just localhost" — keeps the habit consistent regardless of environment.
