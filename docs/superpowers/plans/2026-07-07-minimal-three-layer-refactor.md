# Minimal Three Layer Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Refactor the project to match `ReadME.md`: `Controller -> Service -> Mapper -> PO`, without `ApplicationService`, `InternalService`, or controller-level response converters.

**Architecture:** Each business service interface lives under `application/service` and extends MyBatis-Plus `IService<PO>`. Each implementation lives under `application/service/impl`, extends `ServiceImpl<Mapper, PO>`, owns business logic, transactions, sorting, and PO-to-VO conversion. Controllers depend only on service interfaces and wrap `ResultResponse` / `ResultTableResponse`.

**Tech Stack:** Java 17, Spring Boot 3.5.x, MyBatis-Plus, Spring Security, Jakarta Validation, Maven Wrapper.

---

### Task 1: Inventory and Progress Tracking

**Files:**
- Create: `docs/progress/minimal-three-layer-refactor.md`
- Create: `docs/superpowers/plans/2026-07-07-minimal-three-layer-refactor.md`

- [x] **Step 1: Read `ReadME.md` and identify target architecture**

Run: `sed -n '1,260p' ReadME.md`

Expected: Documentation states `Controller -> Service -> Mapper -> PO`, forbids `InternalService` and `Converter`, and requires unified `ResultResponse` / `ResultTableResponse`.

- [x] **Step 2: Record current service and internal-service inventory**

Run: `find src/main/java/me/link/bootstrap/application/service -maxdepth 1 -type f -name '*ApplicationService.java' | sort`

Expected: Existing `*ApplicationService` files are listed for migration.

### Task 2: Mechanical Service Layer Migration

**Files:**
- Modify: `src/main/java/me/link/bootstrap/application/service/*.java`
- Create: `src/main/java/me/link/bootstrap/application/service/impl/*.java`
- Delete: `src/main/java/me/link/bootstrap/infrastructure/persistence/internal/**/*.java`

- [ ] **Step 1: Convert each `XxxApplicationService` into `XxxServiceImpl`**

Implementation rule:
- Rename class from `XxxApplicationService` to `XxxServiceImpl`.
- Move implementation to `application/service/impl`.
- Extend `ServiceImpl<XxxMapper, XxxPO>`.
- Implement `XxxService`.
- Replace same-aggregate `xxxInternalService.save/getById/page/updateById/removeById/list/exists/count` calls with inherited `save/getById/page/updateById/removeById/list/exists/count`.
- Replace cross-aggregate internal-service dependencies with target service interfaces.

- [ ] **Step 2: Create each `XxxService` interface**

Implementation rule:
- Interface extends `IService<XxxPO>`.
- It declares public business methods used by controllers and other services.
- Methods that formerly returned `XxxPO` to controllers now return `XxxResponseVO`.
- Page methods return `PageResult<XxxResponseVO>`.

### Task 3: Controller Contract Cleanup

**Files:**
- Modify: `src/main/java/me/link/bootstrap/interfaces/controller/*.java`
- Stop using: `src/main/java/me/link/bootstrap/interfaces/converter/ResponseVOConverter.java`

- [ ] **Step 1: Replace `XxxApplicationService` injection with `XxxService`**
- [ ] **Step 2: Remove `ResponseVOConverter` injection**
- [ ] **Step 3: Wrap service-returned VO objects directly**

Expected controller shape:

```java
private final UserService userService;

public ResultResponse<UserResponseVO> get(Long id) {
    return ResultResponse.success(userService.get(id));
}
```

### Task 4: Compile-Driven Cleanup

**Files:**
- Modify any compile failures caused by package moves and renamed services.

- [ ] **Step 1: Run compilation**

Run: `./mvnw -q -DskipTests compile`

Expected: Initial failures identify stale imports and leftover references.

- [ ] **Step 2: Fix stale imports and references**

Rules:
- No references to `*ApplicationService`.
- No references to `*InternalService`.
- No references to `ResponseVOConverter`.

### Task 5: Verification

**Files:**
- Modify: `docs/progress/minimal-three-layer-refactor.md`

- [ ] **Step 1: Run compile verification**

Run: `./mvnw -q -DskipTests compile`

Expected: exit code 0.

- [ ] **Step 2: Run targeted tests if compilation succeeds**

Run: `./mvnw -q test`

Expected: exit code 0, or document concrete failing tests and failure reasons.

- [ ] **Step 3: Record final status**

Update `docs/progress/minimal-three-layer-refactor.md` with completed work, verification commands, and remaining risks.
