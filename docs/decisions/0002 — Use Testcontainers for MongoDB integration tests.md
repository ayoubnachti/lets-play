## Context
Integration tests need to verify repository/query behavior against real MongoDB — mocking the database (Mockito) can't validate actual query correctness, index behavior, or document-shape decisions (embedding vs. referencing, per the schema ADR candidates noted in the roadmap doc). At the same time, pointing tests at the local dev Compose Mongo instance risks shared/stale state between test runs and manual local usage, and doesn't translate cleanly to CI.

## Decision
Use Testcontainers (MongoDB module) for integration tests. Each test class starts a disposable, isolated MongoDB container before its tests run and tears it down after. Unit tests (service-layer logic) continue to use JUnit 5 + Mockito with no database involved at all — Testcontainers is only for the smaller set of tests that specifically exercise repository/persistence behavior.

## Alternatives considered
- **Mock the repository layer (Mockito) for all tests** — rejected: doesn't verify actual MongoDB query behavior, can't catch real N+1 patterns, index issues, or incorrect queries — the mock returns whatever it's told regardless of whether the real query is correct. 
- **Point integration tests at the local dev Compose MongoDB instance** — rejected: shared mutable state between test runs and manual local development, no clean isolation, doesn't translate to CI without separately provisioning a database there.

## Consequences
- **Positive:** integration tests validate real MongoDB behavior; isolated per test class, no shared/stale state; works in CI (GitHub Actions `ubuntu-latest`) with no extra provisioning, since Docker is already available on the runner.
- **Negative:** adds real startup time per test class (container boot), and requires Docker to be running locally for tests to pass — a stopped Docker daemon means confusing test failures rather than a clear "Docker isn't running" message unless handled explicitly. 
- **Follow-up:** as the test suite grows, consider a shared/singleton container pattern across test classes to reduce cumulative startup overhead, rather than reflexively starting a new container per class.
