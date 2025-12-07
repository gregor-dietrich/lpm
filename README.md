# Libre Project Manager

A modern, open-source project management application built with **Quarkus** and **Vaadin**.

## Overview

Libre Project Manager (LPM) is a monolithic full-stack web application designed for teams to manage projects and track work through tickets. It provides a clean, intuitive interface for organizing work, collaborating with team members, and tracking progress.

## Key Features

### User Management

- **Authentication**: Secure login with password hashing (BCrypt)
- **Role-Based Access Control**: Configurable roles with customizable permissions
- **User Profiles**: Self-service profile management
- **Admin Dashboard**: Full user CRUD operations for administrators

### Project Management

- **Project Organization**: Create and manage multiple projects
- **Team Membership**: Add/remove team members with project-specific roles
- **Project Status**: Track projects as Active, Archived, or On Hold
- **Project Keys**: Unique identifiers for ticket numbering (e.g., PROJ-123)

### Ticket Management

- **Ticket Types**: Bug, Feature, Task, Improvement, Epic
- **Priority Levels**: Lowest to Highest priority classification
- **Status Workflow**: Open → In Progress → In Review → Done → Closed
- **Kanban Board**: Visual drag-and-drop ticket management
- **List View**: Filterable and sortable ticket grid
- **Ticket Assignment**: Assign tickets to team members

### Collaboration

- **Comments**: Discussion threads on tickets
- **Activity Log**: Track all changes to tickets and projects
- **Real-time Updates**: Push notifications via WebSocket

## Tech Stack

| Component              | Technology                               |
| ---------------------- | ---------------------------------------- |
| **Backend Framework**  | Quarkus 3.27+                            |
| **Frontend Framework** | Vaadin 24.9+                             |
| **Language**           | Java 21                                  |
| **Database**           | PostgreSQL 18                            |
| **ORM**                | Hibernate with Panache                   |
| **Security**           | Quarkus Security with JPA Identity Store |
| **Build Tool**         | Maven                                    |

## Architecture

LPM follows a monolithic architecture where:

- Vaadin views directly inject backend services via CDI (`@Inject`)
- No REST API boundary between frontend and backend for core functionality
- REST endpoints only for external API integrations (future)
- Entities use Hibernate/Panache for database operations

```txt
┌─────────────────────────────────────────────────────────┐
│                     Vaadin Views                        │
│  (LoginView, DashboardView, ProjectViews, TicketViews) │
└─────────────────────────┬───────────────────────────────┘
                          │ @Inject (CDI)
┌─────────────────────────▼───────────────────────────────┐
│                      Services                           │
│   (UserService, ProjectService, TicketService, etc.)   │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                 Entities (Panache)                      │
│      (User, Project, Ticket, Comment, etc.)            │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│                     PostgreSQL                          │
└─────────────────────────────────────────────────────────┘
```

## Quick Start

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker (for PostgreSQL via Dev Services)

### Development Mode

```bash
# Clone the repository
git clone https://github.com/your-org/lpm.git
cd lpm

# Start in development mode (PostgreSQL starts automatically via Dev Services)
./mvnw quarkus:dev
```

The application will be available at `http://localhost:8080`

### Default Credentials (Development)

| Username  | Password  | Role            |
| --------- | --------- | --------------- |
| admin     | admin     | Administrator   |
| manager   | manager   | Project Manager |
| developer | developer | Developer       |

## Documentation

- [Quick Start Guide](docs/QUICKSTART.md) - Get up and running quickly
- [Build Guide](docs/BUILD_GUIDE.md) - Building for production
- [Implementation Plan](TODO.md) - Development roadmap and task tracking

## Contributing

1. Follow the coding standards in [AGENTS.md](AGENTS.md)
2. Run tests before committing: `./mvnw clean test`
3. Update changelog for user-facing changes

## License

See [LICENSE.md](LICENSE.md).
