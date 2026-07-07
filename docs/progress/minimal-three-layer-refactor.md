# Minimal Three Layer Refactor Progress

Date: 2026-07-07

## Objective

Refactor the codebase according to `ReadME.md` and the latest module-generation rules:

- Use `Controller -> Service -> Mapper -> PO`.
- Remove business reliance on `ApplicationService`, `InternalService`, and controller-level `ResponseVOConverter`.
- Keep unified responses through `ResultResponse<T>` and `ResultTableResponse<T>`.
- Keep sorting whitelists and `PageOrderHelper` mapping in Service implementations.

## Current Status

- [x] Read `ReadME.md`.
- [x] Confirmed current code still contains `*ApplicationService` and `*InternalService`.
- [x] Created implementation plan at `docs/superpowers/plans/2026-07-07-minimal-three-layer-refactor.md`.
- [x] Migrated service interfaces and implementations.
- [x] Updated controllers.
- [x] Removed stale internal service references.
- [x] Ran compile verification.
- [x] Ran tests.
- [x] Ran structural scan for forbidden layer names and converter references.

## Notes

- `ReadME.md` is already modified in the working tree before this refactor; this task treats it as user-owned input and does not rewrite it.
- This refactor is intentionally mechanical and conservative. It preserves package roots, DTO names, VO names, permissions, validation annotations, and existing table mappings unless compilation requires a local correction.
- 2026-07-07: Main code now uses `Controller -> Service -> Mapper -> PO`; old `infrastructure/persistence/internal` and `interfaces/converter` files were removed, and controllers call Service methods returning `ResponseVO`.
- 2026-07-07: `./mvnw -q clean compile` passed.
- 2026-07-07: `./mvnw -q clean -Dmaven.test.skip=false test` passed outside the sandbox. The sandboxed run failed because local Redis access and Mockito ByteBuddy agent attachment were blocked.
- 2026-07-07: Structural scan returned no matches for `ApplicationService`, `InternalService`, `ResponseVOConverter`, `BaseConverter`, `infrastructure.persistence.internal`, or `interfaces.converter` under `src/main/java` and `src/test/java`.
