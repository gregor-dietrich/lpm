# LPM Database Schema

This document describes the database schema for the Libre Project Manager (LPM) application.

## Overview

LPM uses PostgreSQL as its primary database, managed by Hibernate with the Panache pattern. The schema includes:

- **User Management**: Users, roles, and role assignments
- **Project Management**: Projects, members, and project-level configurations
- **Ticket Management**: Tickets, types, priorities, and statuses
- **Collaboration**: Comments and activity logs

## Tables

### Core User Management

#### `users`

Stores user account information.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `username` | VARCHAR(255) | NOT NULL, UNIQUE | Login username |
| `email` | VARCHAR(255) | NOT NULL, UNIQUE | User email address |
| `password_hash` | VARCHAR(255) | NOT NULL | BCrypt hashed password |
| `display_name` | VARCHAR(255) | | User's display name |
| `status` | VARCHAR(50) | NOT NULL, DEFAULT 'ACTIVE' | Account status (ACTIVE, INACTIVE, LOCKED) |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

#### `roles`

Configurable roles that define permission sets. Admins can create custom roles via the UI.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Role name (ADMIN, PROJECT_MANAGER, DEVELOPER, VIEWER) |
| `description` | TEXT | | Role description |
| `color` | VARCHAR(50) | | Display color for UI |
| `icon` | VARCHAR(100) | | Icon identifier for UI |
| `permissions` | TEXT | | JSON array of permissions (e.g., `["*"]` for admin) |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

#### `user_roles`

Junction table linking users to roles (many-to-many relationship).

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `user_id` | BIGINT | PRIMARY KEY, FOREIGN KEY (users) | Reference to user |
| `role_id` | BIGINT | PRIMARY KEY, FOREIGN KEY (roles) | Reference to role |

### Project Management

#### `project_statuses`

Configurable project status values with workflow ordering.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Status name (Active, On Hold, Archived) |
| `description` | TEXT | | Status description |
| `color` | VARCHAR(50) | | Display color for UI |
| `icon` | VARCHAR(100) | | Icon identifier for UI |
| `order` | INTEGER | | Display order in workflow |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

#### `projects`

Stores project information.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `name` | VARCHAR(255) | NOT NULL | Project name |
| `key` | VARCHAR(10) | NOT NULL, UNIQUE | Project key for ticket numbering (e.g., "PROJ") |
| `description` | TEXT | | Project description |
| `owner_id` | BIGINT | NOT NULL, FOREIGN KEY (users) | Project owner |
| `status_id` | BIGINT | FOREIGN KEY (project_statuses) | Current project status |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

#### `project_members`

Tracks team members and their project-specific roles.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `project_id` | BIGINT | NOT NULL, FOREIGN KEY (projects) | Reference to project |
| `user_id` | BIGINT | NOT NULL, FOREIGN KEY (users) | Reference to user |
| `role` | VARCHAR(50) | NOT NULL, DEFAULT 'DEVELOPER' | Project-specific role |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |
| | | UNIQUE (project_id, user_id) | Each user can be a member once per project |

### Ticket Management

#### `ticket_types`

Configurable ticket type values.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Type name (Bug, Feature, Task, Improvement, Epic) |
| `description` | TEXT | | Type description |
| `icon` | VARCHAR(100) | | Icon identifier for UI |
| `color` | VARCHAR(50) | | Display color for UI |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

#### `ticket_priorities`

Configurable ticket priority levels with ordering.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Priority name (Lowest, Low, Medium, High, Highest) |
| `description` | TEXT | | Priority description |
| `color` | VARCHAR(50) | | Display color for UI |
| `icon` | VARCHAR(100) | | Icon identifier for UI |
| `order` | INTEGER | | Display order (1=lowest, 5=highest) |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

#### `ticket_statuses`

Configurable ticket status values with workflow ordering.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `name` | VARCHAR(100) | NOT NULL, UNIQUE | Status name (Open, In Progress, In Review, Done, Closed) |
| `description` | TEXT | | Status description |
| `color` | VARCHAR(50) | | Display color for UI |
| `icon` | VARCHAR(100) | | Icon identifier for UI |
| `order` | INTEGER | | Display order in workflow |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

#### `tickets`

Stores individual tickets/work items.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `project_id` | BIGINT | NOT NULL, FOREIGN KEY (projects) | Reference to parent project |
| `key` | VARCHAR(20) | NOT NULL, UNIQUE | Unique ticket key (e.g., "PROJ-123") |
| `title` | VARCHAR(255) | NOT NULL | Ticket title/summary |
| `description` | TEXT | | Detailed description |
| `type_id` | BIGINT | FOREIGN KEY (ticket_types) | Ticket type (Bug, Feature, etc.) |
| `status_id` | BIGINT | FOREIGN KEY (ticket_statuses) | Current status |
| `priority_id` | BIGINT | FOREIGN KEY (ticket_priorities) | Priority level |
| `assignee_id` | BIGINT | FOREIGN KEY (users) | Assigned user |
| `reporter_id` | BIGINT | NOT NULL, FOREIGN KEY (users) | User who created the ticket |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Record last update timestamp |

### Collaboration

#### `comments`

Stores comments on tickets.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `ticket_id` | BIGINT | NOT NULL, FOREIGN KEY (tickets) | Reference to ticket |
| `author_id` | BIGINT | NOT NULL, FOREIGN KEY (users) | Comment author |
| `content` | TEXT | NOT NULL | Comment text |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Comment creation timestamp |
| `updated_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Comment last update timestamp |

#### `activity_logs`

Audit trail of all changes to entities.

| Column | Type | Constraints | Notes |
|--------|------|-------------|-------|
| `id` | BIGSERIAL | PRIMARY KEY | Auto-generated unique identifier |
| `entity_type` | VARCHAR(100) | NOT NULL | Type of entity (User, Project, Ticket, etc.) |
| `entity_id` | BIGINT | NOT NULL | ID of the entity |
| `action` | VARCHAR(50) | NOT NULL | Action performed (CREATE, UPDATE, DELETE) |
| `user_id` | BIGINT | FOREIGN KEY (users) | User who performed the action |
| `details` | TEXT | | JSON object with details of the change |
| `created_at` | TIMESTAMP | NOT NULL, DEFAULT CURRENT_TIMESTAMP | Timestamp of the action |

## Indexes

The following indexes should be created for optimal query performance:

```sql
-- User lookups
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);

-- Project lookups
CREATE INDEX idx_projects_owner_id ON projects(owner_id);
CREATE INDEX idx_projects_key ON projects("key");
CREATE INDEX idx_projects_status_id ON projects(status_id);

-- Project member lookups
CREATE INDEX idx_project_members_project_id ON project_members(project_id);
CREATE INDEX idx_project_members_user_id ON project_members(user_id);

-- Ticket lookups
CREATE INDEX idx_tickets_project_id ON tickets(project_id);
CREATE INDEX idx_tickets_key ON tickets("key");
CREATE INDEX idx_tickets_status_id ON tickets(status_id);
CREATE INDEX idx_tickets_assignee_id ON tickets(assignee_id);
CREATE INDEX idx_tickets_reporter_id ON tickets(reporter_id);
CREATE INDEX idx_tickets_type_id ON tickets(type_id);
CREATE INDEX idx_tickets_priority_id ON tickets(priority_id);

-- Comment lookups
CREATE INDEX idx_comments_ticket_id ON comments(ticket_id);
CREATE INDEX idx_comments_author_id ON comments(author_id);

-- Activity log lookups
CREATE INDEX idx_activity_logs_entity ON activity_logs(entity_type, entity_id);
CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);
CREATE INDEX idx_activity_logs_created_at ON activity_logs(created_at);
```

## Development

In development mode, the schema is automatically created by Hibernate (`drop-and-create` mode) and seed data is loaded from `import.sql`.

To reset the database during development:
1. Stop the dev server
2. The next `mvn quarkus:dev` will recreate the schema and reload seed data

## Production

In production mode, Hibernate uses `validate` mode - it validates the schema matches the entities but does not create or drop tables. Database migrations should be managed separately using tools like Flyway or Liquibase.
