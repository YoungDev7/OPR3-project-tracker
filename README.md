# check VBAP branch for VBAP version

# OPR3 Project

This is a Spring Boot application for project and task management with JWT-based authentication.

## Prerequisites

- Java 21
- Maven 3.6+
- Docker and Docker Compose (for containerized setup)
- MySQL database

## Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd opr3
   ```

2. Configure environment variables:
   - Copy `application-dev.properties.example` to `application-dev.properties` and fill in the required values (database credentials, JWT secrets).
   - For Docker, copy `.env.example` to `.env` and configure the variables.

3. Set up the database:
   - Ensure MySQL is running.
   - The application will create tables automatically on startup.

## Running the Application

### Local Development (with Maven)

1. Build the project:
   ```bash
   ./mvnw clean install
   ```

2. Run the application:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. The application will be available at `http://localhost:8080`.


## API Endpoints

- Authentication: `/api/auth`
  - POST `/authenticate` - Login
  - POST `/register` - Register
  - POST `/refresh` - Refresh token
  - POST `/logout` - Logout
  - GET `/validateToken` - Validate token

- Projects: `/api/projects`
  - GET `/` - Get all user projects
  - POST `/` - Create project
  - GET `/{projectId}` - Get project by ID
  - PUT `/{projectId}` - Update project
  - PATCH `/{projectId}/archive` - Archive project

- Tasks: `/api/tasks` (under projects)
  - POST `/` - Create task
  - GET `/{taskId}` - Get task by ID
  - PUT `/{taskId}` - Update task
  - DELETE `/{taskId}` - Delete task
  - PATCH `/{taskId}/status` - Update task status

## Testing

Run tests with Maven:
```bash
./mvnw test
```

## Configuration

- Profiles: `dev`, `test`, `docker_dev`
- CORS allowed origins: Configured in `CorsConfig.java`
- JWT expiration: Configurable in properties


