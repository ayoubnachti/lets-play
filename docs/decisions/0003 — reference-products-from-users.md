## Context
User and Product have a one-to-many relationship (one User owns many Products).
MongoDB supports two natural ways to model this: embedding Products as
subdocuments inside the User document, or referencing them as independent
documents in their own collection via a `userId` field. The brief requires
`GET /products` as a public endpoint returning products across all users,
and `PUT/DELETE /products/{id}` scoped to individual products by their own ID.

## Decision
Model Product as its own top-level document in a separate `products`
collection, holding a `userId` field that references the owning User's `_id`.
Do not embed Products inside the User document.

## Alternatives considered
- **Embed Products inside User** — rejected: makes the public "list all
  products" endpoint require fetching and flattening every user's embedded
  array instead of a direct collection query; makes individually addressing
  a product by ID for update/delete awkward with Spring Data MongoDB's
  repository patterns; risks the 16MB document size ceiling if a user
  accumulates many products (not a real risk at this scale, but the
  textbook failure mode for embedding unbounded "many" sides).

## Consequences
- Positive: `GET /products` is a simple, direct, indexable query; Products
  are independently addressable by their own `_id` for owner/admin-scoped
  update and delete; no document growth ceiling risk.
- Negative: no native join — fetching a user with all their products
  requires either a `$lookup` or a separate query, not a single document
  read. Not a real cost here given the access patterns in this project.
- Follow-up: index `userId` on the `products` collection once the "get my
  own products" or admin-listing-by-user query exists — flag as a ticket
  when that endpoint is built, not before it's needed.
