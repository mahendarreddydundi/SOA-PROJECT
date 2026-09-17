# ResolveNow Backend

ResolveNow is a Java 17, Spring Boot, PostgreSQL microservices platform with a React and TypeScript operations console. It contains Eureka, Gateway, Auth, Complaint, Assignment, and Notification services.

## Recommended: GitHub Codespaces

Codespaces runs the tools in the cloud, so Docker, Java, Maven, and PostgreSQL do not use RAM on your computer.

1. Push this repository to GitHub.
2. Open the repository and choose **Code > Codespaces > Create codespace on main**.
3. Use a machine with at least 4 GB RAM. Use 8 GB if the full Compose stack is slow.
4. In the Codespaces terminal, run:

```bash
mvn -DskipTests package
docker compose up --build
```

If the Docker environment blocks container-to-container bridge traffic, use the host-network override instead:

```bash
docker compose -f docker-compose.yml -f docker-compose.host.yml up --build
```

The host-network fallback also serves the frontend on `http://localhost:5173`.

5. Open port `8080` from the Ports panel. The API Gateway is the client entry point.
Verify it is ready before making API requests:

```bash
curl http://localhost:8080/actuator/health
```

The expected response is `{"status":"UP"}`. The gateway is an API entry point, so opening `/` directly does not display a web page.

The Compose frontend is available on port `5173`; open the forwarded `5173` port to use the operations console. It forwards `/api` requests to the Gateway internally.

To run the operations console locally during development, start the frontend in a separate terminal:

```bash
cd frontend
npm install
npm run dev
```

The Vite development server proxies `/api` requests to the Gateway at `http://localhost:8080`.

Useful endpoints:

```text
POST http://localhost:8080/auth/signup
POST http://localhost:8080/auth/login
GET  http://localhost:8080/complaints
POST http://localhost:8080/complaints
```

Use the JWT returned by login as an `Authorization: Bearer <token>` header for protected gateway requests.

## Temporary option: Play with Docker

Play with Docker is useful for a short classroom demo. It is temporary, may expire after a few hours, and its storage is not a reliable place for PostgreSQL data.

1. Open https://labs.play-with-docker.com/.
2. Sign in with Docker Hub.
3. Create one instance.
4. Clone this repository into the instance.
5. Run:

```bash
git clone <your-github-repository-url>
cd <repository-folder>
docker compose up --build
```

Use the exposed port `8080` link for the Gateway. Do not use this option for production or important data.

## Lower-memory cloud setup

For a cheaper setup, run only the Java services in Codespaces and use hosted PostgreSQL databases such as Neon or Supabase. Create one database per service and set these variables for each service:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
EUREKA_URL
JWT_SECRET
```

The current local defaults use PostgreSQL ports `5433` through `5436`. In Docker Compose, each service connects to its own PostgreSQL container on port `5432` inside the Compose network.

## Local development without Docker

This requires Java 17, Maven, and four PostgreSQL databases. If the computer does not have enough RAM, use Codespaces instead.

```bash
mvn clean package
mvn -pl eureka-server spring-boot:run
mvn -pl auth-service spring-boot:run
mvn -pl complaint-service spring-boot:run
mvn -pl assignment-service spring-boot:run
mvn -pl notification-service spring-boot:run
mvn -pl api-gateway spring-boot:run
```

Start Eureka first, then the other services, and use the Gateway on port `8080`.

## Important limitation

An online Docker playground does not remove the need for a build environment; it only moves the build to the cloud. Codespaces is the best choice when you need to edit, test, and keep the project available. Play with Docker is best for a temporary demonstration.
