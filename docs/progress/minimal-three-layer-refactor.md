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
- [x] Normalized REST resource paths to plural nouns under `/api/v1`.
- [x] Synchronized frontend API contract documentation.
- [x] Removed remaining forbidden `Repository` naming from shared component code.
- [x] Rechecked REST method usage, mapper inheritance, and unused refactor leftovers.
- [x] Aligned Mapper package path with `ReadME.md` (`infrastructure/mapper`).
- [x] Added user-side community write permissions and DML permission seeds.
- [x] Removed duplicate Java imports left by the earlier mechanical refactor.

## Notes

- `ReadME.md` is already modified in the working tree before this refactor; this task treats it as user-owned input and does not rewrite it.
- This refactor is intentionally mechanical and conservative. It preserves package roots, DTO names, VO names, permissions, validation annotations, and existing table mappings unless compilation requires a local correction.
- 2026-07-07: Main code now uses `Controller -> Service -> Mapper -> PO`; old `infrastructure/persistence/internal` and `interfaces/converter` files were removed, and controllers call Service methods returning `ResponseVO`.
- 2026-07-07: `./mvnw -q clean compile` passed.
- 2026-07-07: `./mvnw -q clean -Dmaven.test.skip=false test` passed outside the sandbox. The sandboxed run failed because local Redis access and Mockito ByteBuddy agent attachment were blocked.
- 2026-07-07: Structural scan returned no matches for `ApplicationService`, `InternalService`, `ResponseVOConverter`, `BaseConverter`, `infrastructure.persistence.internal`, or `interfaces.converter` under `src/main/java` and `src/test/java`.
- 2026-07-07: REST controller paths were normalized to plural resources, including `/system/tenants`, `/system/tenant-packages`, `/system/roles`, `/system/menus`, `/system/organizations`, `/system/user-roles`, `/system/role-menus`, `/system/operate-logs`, and community post interaction subresources `/likes`, `/collections`, `/interactions/current`.
- 2026-07-07: `docs/frontend-api-contract.md` was updated to match the normalized API paths.
- 2026-07-07: `BloomRepository` was renamed to `BloomFilterStore` to remove the remaining forbidden `Repository` suffix from source files.
- 2026-07-07: Fixed-string scans returned no old single-resource path literals for tenant, tenant package, role, menu, organization, and user-role endpoints.
- 2026-07-07: Forbidden file-name scan returned no matches for `ApplicationService`, `InternalService`, `Repository`, `RepositoryImpl`, `Command`, `Query`, or `Converter` under `src/main/java` and `src/test/java`.
- 2026-07-07: `./mvnw -q clean compile`, `./mvnw -q clean -Dmaven.test.skip=false test`, and `git diff --check` passed after the continued refactor.
- 2026-07-07: `PATCH /api/v1/auth/tokens/current` was changed to `PUT /api/v1/auth/tokens/current`, and explicit PATCH support was removed from CORS defaults, API crypto body-method handling, and operation-log method mapping.
- 2026-07-07: `PermissionMapper` now extends `BaseMapper<MenuPO>` while keeping only the custom permission aggregation SQL methods used by auth/permission cache flows.
- 2026-07-07: Removed unused `LoginResponseVO` and `TokenRefreshResult` leftovers after confirming they had no references.
- 2026-07-07: Follow-up verification passed: `./mvnw -q clean compile`, `./mvnw -q clean -Dmaven.test.skip=false test`, `git diff --check`, PATCH/source leftover scan, and forbidden file-name scan.
- 2026-07-07: Continued audit found Mapper interfaces still under `infrastructure/persistence/mapper`, while `ReadME.md` requires `infrastructure/mapper`. Mappers were moved, `@MapperScan` was updated, and XML namespaces now point to `me.link.bootstrap.infrastructure.mapper`.
- 2026-07-07: User-side community write endpoints now declare Spring Security permission checks with `community:*:*` permissions. Matching hidden permission rows were added to `src/sql/mysql/link-DML-v0.1.sql` and granted to tenant admin/basic user seed roles so initialized environments can authorize the endpoints.
- 2026-07-07: Duplicate import scan is clean after removing repeated imports from Service interfaces and implementations.
- 2026-07-07: Verification after this audit: `./mvnw -q clean compile` passed; `git diff --check` passed; scans returned no old Mapper package, PATCH/token leftovers, forbidden layer file names, or duplicate imports. Full `./mvnw -q clean -Dmaven.test.skip=false test` currently fails only at `LinkMainApplicationTests.contextLoads` because Redis at `127.0.0.1:6379` is unavailable. The remaining 7 test classes pass when run explicitly.
- 2026-07-07: Continued audit against `ReadME.md` found no forbidden layer file names, controller `try/catch`, controller field injection, stale PATCH endpoints, or old mapper package references.
- 2026-07-07: MySQL Dockerfile no longer bakes `MYSQL_ROOT_PASSWORD` or `MYSQL_DATABASE` into the image; compose and documented `docker run` commands provide them at runtime.
- 2026-07-07: Tenant package seed `menu_ids` now includes user-side community write permission IDs `320-329`, and the `tenant_user` seed description was updated from read-only to community participation.
- 2026-07-07: Verification after this check: `docker compose -f docker/docker-compose.yml config`, `git diff --check`, and `./mvnw -q clean compile` passed. Controller-only scan returned no field injection, local `try/catch`, or manual `new ResultResponse` / `new ResultTableResponse` construction.
- 2026-07-07: Security migration started from Sa-Token to Spring Security + Redis opaque Bearer Token. Added `docs/spring-security-opaque-token.md`, replaced controller permission annotations with `@PreAuthorize`, and introduced Redis-backed `SecurityTokenSessionService`.
- 2026-07-07: Security migration completed in runtime code. `AuthServiceImpl` now issues, refreshes, inspects, and revokes Redis-backed opaque Bearer tokens; `SecurityHelper`, tenant field fill, and operation-log audit read the current user from `SecurityContextHolder`.
- 2026-07-07: Removed Sa-Token Maven dependencies, config sections, MVC interceptor, and permission bridge. Source/config scan under `pom.xml`, `src/main/java`, `src/test/java`, and `src/main/resources` returns no Sa-Token runtime references.
- 2026-07-07: Added focused tests for Redis token session lifecycle and Spring Security context helper behavior. Test resources force Mockito's subclass mock maker so local JDKs without working dynamic agent attachment can run the suite.
- 2026-07-07: Final verification for the Spring Security token migration passed: `./mvnw -q -DskipTests compile`, `./mvnw -q -Dmaven.test.skip=false test` outside the sandbox with local Redis access, `git diff --check`, and Sa-Token runtime reference scan. The same full test command fails inside the restricted sandbox only because socket access to `127.0.0.1:6379` is denied.
