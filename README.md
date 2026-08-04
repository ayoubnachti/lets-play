# lets-play

A RESTful CRUD API for a small e-commerce-like platform, built with Spring
Boot and MongoDB. Manages Users and Products, with JWT-based authentication
and role-based access control (admin vs. user).

## Stack

- Spring Boot 4.1, Spring Data MongoDB
- Spring Security + JWT (hand-rolled `JwtAuthenticationFilter`)
- MongoDB 8.0
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, Testcontainers
- Docker / Docker Compose

## Prerequisites

- **Docker & Docker Compose** — the recommended way to run this project,
  covers both the app and MongoDB.
- **JDK 21** — only needed if you want to run the app directly with Maven
  instead of Docker.

## Setup

1. Copy the env template and fill in real values:
   ```
   cp .env.example .env
   ```
2. Generate a JWT signing secret:
   ```
   openssl rand -base64 32
   ```
   Paste the result into `JWT_SECRET` in `.env`.
3. Set `ADMIN_PASSWORD` (and optionally change `ADMIN_EMAIL`) in `.env`.
   On first startup, an admin account is seeded automatically from these
   values — safe to leave running, it's a no-op on every startup after
   the first (it checks whether the account already exists first).

`.env` is gitignored — never commit real secrets.

## HTTPS (optional, local demo)

Off by default — the app runs over plain HTTP unless you explicitly enable
this. To demo TLS locally with a self-signed certificate:

```
keytool -genkeypair -alias letsplay -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

Run this from the project root (`keystore.p12` is gitignored — never
commit it). Then in `.env`, set:

```
SSL_ENABLED=true
SSL_KEYSTORE_PASSWORD=<the password you set above>
```

The app will then be reachable at `https://localhost:8080` instead of
`http://`. Since it's self-signed, browsers will show a security warning,
and Postman/curl need certificate verification disabled to connect
(`curl -k`, or the equivalent toggle in Postman) — expected for a
self-signed cert with no CA behind it.

## Running with Docker (recommended)

```
docker compose up --build
```

This builds the app image and starts both the app and MongoDB together.
The app will be available at `http://localhost:8080` once both containers
are up.

## Running locally without Docker

Requires a MongoDB instance reachable at the URI in `.env`
(`MONGODB_URI`, defaults to `mongodb://localhost:27017/lets-play` — you can
start just the database with `docker compose up mongodb` and run the app
itself directly).

```
./mvnw spring-boot:run
```

`.env` is loaded automatically via `spring-dotenv` for local runs. Real
environment variables always take precedence over `.env`, so this behaves
identically to the Docker/CI setup either way.

## Running tests

```
./mvnw clean test
```

Requires Docker to be running locally — a smoke test uses Testcontainers
to spin up a real, disposable MongoDB and verify the app actually
connects and can read/write against it. The rest of the suite is unit
tests (JUnit 5 + Mockito) with no real database involved. Tests use their
own dummy secrets (`application-test.yaml`, active under the `test`
profile) and never touch your real `.env`.

## API documentation

Once the app is running:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Raw OpenAPI spec: `http://localhost:8080/v3/api-docs`

Most endpoints require a JWT. Register or log in via `/auth/register` or
`/auth/login` to get a token, then click **Authorize** in Swagger UI and
paste it in to test protected endpoints directly from the docs page.

## Roles & access

| Endpoint | Access |
|---|---|
| `GET /products`, `GET /products/{id}` | Public |
| `POST /products` | Any authenticated user |
| `PUT /products/{id}`, `DELETE /products/{id}` | Product owner or admin |
| `POST /auth/register`, `POST /auth/login` | Public |
| `GET /users`, `GET /users/{id}`, `PUT /users/{id}`, `DELETE /users/{id}` | Admin only |