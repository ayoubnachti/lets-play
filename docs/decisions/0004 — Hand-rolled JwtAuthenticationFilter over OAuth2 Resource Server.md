## Context
POST /products (and future protected endpoints) need to authenticate incoming requests via the JWT issued by AuthService/JwtService during login. Two realistic approaches exist in Spring Security: write a custom OncePerRequestFilter that reuses the existing JwtService validation logic, or adopt Spring Security's OAuth2 Resource Server support, which supplies its own filter, JwtDecoder-based validation, and JWT-to-Authentication mapping out of the box.

## Decision
Implement a custom OncePerRequestFilter (JwtAuthenticationFilter) that extracts the Bearer token, delegates validation to the existing JwtService, and manually populates SecurityContextHolder with the resulting Authentication.

## Alternatives considered
- **Spring Security OAuth2 Resource Server** (`spring-boot-starter-oauth2-resource-server`, `JwtDecoder` + `JwtAuthenticationConverter`) — rejected: would introduce a second, independent JWT validation path (JwtDecoder) alongside the existing JwtService, making JwtService's validation logic dead code for incoming requests. Also requires a new conversion layer (JwtAuthenticationConverter, ROLE_/SCOPE_ authority mapping) purely to reproduce what JwtService already does.

## Consequences
- Positive: single source of truth for JWT validation (JwtService, used for both issuance and now incoming validation); filter logic fully visible and steppable within the codebase, with no new dependency. 
- Negative: no automatic spec-correct WWW-Authenticate header on 401s — must be handled explicitly if that level of correctness matters later. Reimplements a small amount of logic (header parsing, SecurityContext population) that a framework filter would otherwise provide. 
- Follow-up: if this project ever needs to accept tokens from an external identity provider (not just its own AuthService), this decision should be revisited — Resource Server support becomes far more valuable in a multi-issuer scenario.