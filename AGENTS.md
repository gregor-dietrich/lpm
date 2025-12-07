---
applyTo: "**"
---

# LPM Project Coding & Architecture Guide

## Overview

LPM is a monolithic full-stack web application built with **Quarkus** (backend) and **Vaadin** (frontend). The project tightly integrates backend and frontend using CDI (`@Inject`), with no REST API boundary between core services and views.

## Key Features

- **User Management**: Authentication, role-based access control (ADMIN, PROJECT_MANAGER, DEVELOPER, VIEWER), user profiles
- **Project Management**: Create/manage projects, team membership, project status tracking
- **Ticket Management**: Full ticket lifecycle with types (Bug, Feature, Task, etc.), priorities, and status workflow
- **Collaboration**: Comments on tickets, activity logging, real-time updates via WebSocket

## Project Structure

### Architecture Pattern

`Vaadin Views → Services (CDI @Inject) → Entities (Panache) → PostgreSQL`

- **Backend & Frontend:**
  - Vaadin views directly inject backend services (CDI, `@Inject`).
  - No REST API for core app logic; REST only for external APIs.
  - Use `@Push` for real-time UI updates.

### Package Organization

```txt
de.vptr.lpm/
├── entity/          # JPA entities extending AbstractEntity
├── dto/             # Data transfer objects (no sensitive data)
├── enums/           # Enumeration types
├── service/         # Business logic services
├── security/        # Security utilities (password hashing, etc.)
└── view/            # Vaadin UI views organized by feature
    ├── user/        # User management views
    ├── project/     # Project management views
    └── ticket/      # Ticket management views
```

### Configurable vs Hardcoded Values

**Configurable entities** (stored in database, admin can modify):

- `Role` - User roles with permissions
- `TicketStatus` - Ticket workflow statuses with transitions
- `TicketPriority` - Priority levels with ordering
- `TicketType` - Ticket types (Bug, Feature, Task, etc.)
- `ProjectStatus` - Project status values

**Hardcoded enums** (system-level, code changes required):

- `UserStatus` - Account states (ACTIVE, INACTIVE, LOCKED)

Configurable entities should have: `name`, `description`, `color`, `icon`, `order` (for sorting), and any entity-specific fields like workflow transitions.

### Entity Guidelines

- **All entities** must extend `AbstractEntity` which provides:
  - `id` (Long, auto-generated)
  - `createdAt` (LocalDateTime, set on persist)
  - `updatedAt` (LocalDateTime, set on update)
- Use Panache active record pattern for simple CRUD
- Add `@Entity`, `@Table` annotations with explicit table names
- Use `@NotNull`, `@Size`, `@Email` for validation

### DTO Guidelines

- Create DTOs for all entities to avoid exposing sensitive data
- Never include password hashes in DTOs
- Use static factory methods: `fromEntity(Entity e)` and `toEntity()`
- DTOs should be records or immutable classes when possible

### Service Guidelines

- Services are `@ApplicationScoped` CDI beans
- Use `@Transactional` for write operations
- Return DTOs from public methods, not entities
- Inject other services via constructor or `@Inject`
- Handle business logic validation in services, not views

### View Guidelines

- Views use `@Route` annotation with path
- Extend appropriate Vaadin layout classes
- Inject services directly via `@Inject`
- Use `@RolesAllowed` for access control
- Implement `HasUrlParameter<T>` for parameterized routes
- Use dialogs for create/edit forms

## User Management & Permissions

### Roles

Roles are **configurable entities** stored in the database, not hardcoded enums. Default seed data includes:

| Role              | Description          | Default Permissions                             |
| ----------------- | -------------------- | ----------------------------------------------- |
| `ADMIN`           | System administrator | Full access, user/role management               |
| `PROJECT_MANAGER` | Project lead         | Create projects, manage members, all ticket ops |
| `DEVELOPER`       | Team member          | View projects, create/edit assigned tickets     |
| `VIEWER`          | Read-only access     | View projects and tickets only                  |

Admins can create custom roles with specific permissions via the Role Management UI.

### Security Annotations

```java
@RolesAllowed("ADMIN")           // Admin only
@RolesAllowed({"ADMIN", "PROJECT_MANAGER"})  // Multiple roles
@PermitAll                        // Any authenticated user
@DenyAll                          // No access (default deny)
```

### Password Handling

- Use BCrypt for password hashing via `PasswordUtil`
- Never store plain-text passwords
- Never log passwords or hashes

## Database Conventions

### Table Naming

- Use lowercase with underscores: `project_members`
- Prefix junction tables with both entity names: `project_member`

### Column Naming

- Use lowercase with underscores: `created_at`, `password_hash`
- Foreign keys: `<entity>_id` (e.g., `project_id`, `user_id`)

### Seed Data

- Development seed data in `src/main/resources/sql/import.sql`
- Default users: `admin/admin`, `manager/manager`, `developer/developer`

## Testing Standards

### Unit Tests

- Test services in isolation with mocked dependencies
- Use `@QuarkusTest` for integration tests
- Use `@InjectMock` for mocking CDI beans
- Mock external services and database for unit tests

### Integration Tests

- Test full request flows with database
- Use Testcontainers for PostgreSQL
- Test authentication and authorization

### Running Tests

```bash
./mvnw clean test           # Run all tests
./mvnw test -Dtest=UserServiceTest  # Run specific test
```

## Code Style

### General

- Always assign `final` where possible.
- Always use `var` where possible.
- Never use fully qualified class names; use imports instead.
- Follow established conventions and styles when adding or editing code.
- Ensure proper error handling and logging at all times.
- Ensure proper test coverage.
- Do not leave "TODO" or similar comments anywhere instead of actual implementation.
- Do not hallucinate and do not make up factual information. Consult project and library/framework documentation as needed instead.

### Checkstyle

- Project uses Checkstyle with custom rules in `checkstyle.xml`
- Run `./mvnw checkstyle:check` to verify

### SpotBugs

- Static analysis configured in `spotbugs-exclude.xml`
- Run `./mvnw spotbugs:check` to verify

### Naming Conventions

- **Classes**: PascalCase (`UserService`, `TicketDetailView`)
- **Methods**: camelCase (`findByUsername`, `createTicket`)
- **Constants**: UPPER_SNAKE_CASE (`DEFAULT_PAGE_SIZE`)
- **Packages**: lowercase (`de.vptr.lpm.service`)

### Javadoc

- Required for all public classes and methods
- Include `@param`, `@return`, `@throws` tags
- Keep descriptions concise but informative

## Development Workflow

1. Check [TODO.md](TODO.md) for current tasks and priorities
2. Create feature branch from `main`
3. Implement changes following these guidelines
4. Add/update tests for new functionality
5. Run `./mvnw clean verify` to check compilation, tests, and code quality
6. Update changelog for user-facing changes
7. Submit PR for review

## Configuration & Environment

### Application Properties

- **Main config:** `src/main/resources/application.properties`
- Use `%dev.` prefix for dev-only settings
- Use `%test.` prefix for test-only settings
- Use `%prod.` prefix for production settings

### Database

- **Development**: PostgreSQL via Quarkus Dev Services (auto-started)
- **Production**: Configure via environment variables or `application.properties`
- Schema managed by Hibernate (`drop-and-create` in dev, `validate` in prod)

### Logging

- Console logging in development
- File logging in production to `logs/lpm.log`
- JSON format available for log aggregation

## Documentation

- [README.md](README.md) - Project overview and quick start
- [TODO.md](TODO.md) - Implementation plan and task tracking
- [docs/QUICKSTART.md](docs/QUICKSTART.md) - Detailed setup guide
- [docs/BUILD_GUIDE.md](docs/BUILD_GUIDE.md) - Production build instructions

## Changelog Standards

**Changelog Directory:** All changelogs are maintained in the `changelog/` directory, with a separate `.md` file for each released version (e.g., `changelog/1.0.0.md`, `changelog/1.1.0.md`).

**Format:** Each changelog file follows [Semantic Versioning 2.0.0](https://www.semver.org) and [Keep a Changelog 1.1.0](https://www.keepachangelog.com).

**Content:**

- Each entry describes changes from an end user's perspective (features, fixes, removals, breaking changes, etc.).
- Do **not** mention class, method, or file names—focus on what changed for users, not implementation details.
- Document all changes introduced by commits since the last tagged release in a new version file.
- Use clear sections: Added, Changed, Deprecated, Removed, Fixed, Security, and **Unreleased**.
- The **Unreleased** section lists upcoming changes that are not yet released, so users can see what to expect. At release time, move these changes into the new version section.
- Update the changelog with every meaningful change before merging or tagging a release.
- For initial releases, provide a brief explanation of the changelog and versioning approach.

## Common Patterns

### Creating a New Feature

1. **Entity**: Create in `entity/` extending `AbstractEntity`
2. **Enums**: Create enums in `enums/`
3. **DTO**: Create in `dto/` with `fromEntity()` method
4. **Service**: Create in `service/` with `@ApplicationScoped`
5. **Views**: Create in `view/<feature>/` with `@Route`
6. **Tests**: Add unit tests for service, integration tests for views

### Adding a New View

```java
@Route(value = "feature", layout = MainLayout.class)
@PageTitle("Feature | LPM")
@RolesAllowed({"ADMIN", "PROJECT_MANAGER"})
public class FeatureView extends VerticalLayout {

    @Inject
    FeatureService featureService;

    public FeatureView() {
        // Initialize UI components
    }
}
```

### Service Method Pattern

```java
@ApplicationScoped
public class FeatureService {

    @Transactional
    public FeatureDTO create(FeatureDTO dto) {
        // Validate
        // Convert to entity
        // Persist
        // Return DTO
    }

    public Optional<FeatureDTO> findById(Long id) {
        return Feature.findByIdOptional(id)
            .map(FeatureDTO::fromEntity);
    }
}
```
