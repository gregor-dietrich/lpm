# 🚀 Quickstart

## 🧑‍💻 Development Mode

```sh
git clone git@github.com:gregor-dietrich/lpm.git
cd lpm
make install   # Installs dependencies, skips tests
make dev       # Starts Quarkus in dev mode
```

See the [Makefile](../Makefile) or use `make help` for all available commands.

### 🧪 Running Tests

> **_NOTE:_** You need to do this in the same directory you cloned the repository into.

```sh
make install
make test
```

## 🏭 Production Mode

### Using Docker

> **_NOTE:_** Change `localhost` to the actual hostname where your PostgreSQL instance is running.

```sh
docker run -d --name lpm \
  -p 80:9001 \
  -e quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/lpm \
  -e quarkus.datasource.username=lpm \
  -e quarkus.datasource.password=changeit \
  -e GEMINI_API_KEY=your_gemini_api_key \
  gregordietrich/lpm:1.0.0-SNAPSHOT
```

### Using Docker Compose

#### 1. Create a `.env` file in the same directory

```properties
# Database
SQL_DATABASE=lpm
SQL_USERNAME=lpm
SQL_PASSWORD=changeit
PGADMIN_EMAIL=your@email.com
PGADMIN_PASSWORD=safe_password_here
```

#### 2. Save the `import.sql` file in the same directory

You can save it from here: <https://github.com/gregor-dietrich/lpm/blob/main/src/main/resources/import.sql>

#### 3. Create a `docker-compose.yml` file in the same directory

```yml
services:
  lpm:
    image: gregordietrich/lpm:1.0.0-SNAPSHOT
    restart: unless-stopped
    env_file:
      - .env
    environment:
      quarkus.datasource.jdbc.url: jdbc:postgresql://postgres:5432/${SQL_DATABASE:-lpm}
      quarkus.datasource.username: ${SQL_USERNAME:-lpm}
      quarkus.datasource.password: ${SQL_PASSWORD:-changeit}
    ports:
      - "80:8080"
    volumes:
      - lpm_logs:/deployments/logs
    healthcheck:
      test: ["CMD-SHELL", "wget --no-verbose --tries=1 --spider http://lpm:8080/q/health || exit 1"]
      interval: 10s
      timeout: 3s
      retries: 3
      start_period: 10s
    depends_on:
      postgres:
        condition: service_healthy

  postgres:
    image: postgres:18.1-alpine3.23
    restart: unless-stopped
    command: ["postgres", "-c", "max_connections=200"]
    environment:
      POSTGRES_USER: ${SQL_USERNAME:-lpm}
      POSTGRES_PASSWORD: ${SQL_PASSWORD:-changeit}
      POSTGRES_DB: ${SQL_DATABASE:-lpm}
      POSTGRES_PORT: 5432
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./import.sql:/docker-entrypoint-initdb.d/import.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${SQL_USERNAME:-lpm} -d ${SQL_DATABASE:-lpm}"]
      interval: 10s
      timeout: 3s
      retries: 3
      start_period: 5s

  pgadmin:
    image: dpage/pgadmin4:9.10.0
    restart: unless-stopped
    environment:
      PGADMIN_DEFAULT_EMAIL: ${PGADMIN_EMAIL:-admin@example.com}
      PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_PASSWORD:-changeit}
    ports:
      - "42069:80"
    volumes:
      - pgadmin_data:/var/lib/pgadmin
    healthcheck:
      test: ["CMD-SHELL", "wget --no-verbose --tries=1 --spider -q http://pgadmin/ || exit 1"]
      interval: 10s
      timeout: 3s
      retries: 3
      start_period: 10s
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  lpm_logs:
  pgadmin_data:
  postgres_data:
```

#### 4. Start the compose stack

> **_NOTE:_** You need to do this in the same directory where you saved the `docker-compose.yml` file above.

```sh
docker compose up -d
```
