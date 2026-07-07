# Spring Security Redis Opaque Bearer Token

Date: 2026-07-07

## Objective

Replace Sa-Token with Spring Security while keeping the existing REST contract:

- Client continues to send `Authorization: Bearer <token>`.
- Token is an opaque random value, not JWT.
- Token session state is stored in Redis through Redisson.
- Controller permission checks use `@PreAuthorize("hasAuthority('permission:code')")`.

## Runtime Flow

1. `AuthServiceImpl` validates credentials or email code.
2. `SecurityTokenSessionService` loads permission codes and role codes from `PermissionCacheService`.
3. A random opaque token is generated and stored at `link:security:token:{token}`.
4. `BearerTokenAuthenticationFilter` resolves the Bearer token on each request.
5. A valid Redis session is converted into `LoginUserPrincipal` and Spring Security authorities.
6. `SecurityHelper`, tenant isolation, audit fill, and operation logs read the current user from `SecurityContextHolder`.

## Redis Session Fields

`SecurityTokenSession` stores only server-side authentication context:

- `token`
- `userId`
- `tenantId`
- `userType`
- `superAdmin`
- `permissions`
- `roles`
- `createdAtEpochSecond`
- `lastActiveAtEpochSecond`
- `expireAtEpochSecond`

The absolute token lifetime and idle lifetime are both enforced by `SecurityTokenSessionService`.

## Configuration

Shared defaults live under `link.security.token`:

```yaml
link:
  security:
    token:
      token-name: Authorization
      token-prefix: Bearer
      timeout: 1h
      active-timeout: 30m
      auto-renew: true
      key-prefix: link:security:token:
```

Production overrides the default lifetime to `2h` absolute and `1h` idle unless environment variables override them.

## Migration Notes

- Removed `sa-token-spring-boot3-starter` and `sa-token-redis-jackson`.
- Added `spring-boot-starter-security` and `spring-security-test`.
- Removed the MVC `SaInterceptor` and `StpInterfaceImpl`.
- Replaced `@SaCheckPermission` with Spring Security method authorization.
- Updated 401/403 handling to Spring Security `AuthenticationEntryPoint`, `AccessDeniedHandler`, and advice fallback handlers.

## Verification

- `SecurityTokenSessionServiceTest` covers token creation, idle expiration, and refresh.
- `SecurityHelperTest` covers current-user resolution from `SecurityContextHolder`.
- `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` uses Mockito's subclass mock maker to avoid JDK dynamic-agent attachment requirements in local test runs.
