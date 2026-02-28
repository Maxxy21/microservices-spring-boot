# Microservices Quiz Platform

A microservices-based quiz platform built with Java 21, Spring Boot 4, and Spring Cloud. The system allows creating categorized quizzes, serving questions, and calculating scores across independently deployable services.

## Architecture

```
                        ┌─────────────────┐
                        │   API Gateway   │
                        │   (port 8091)   │
                        └────────┬────────┘
                                 │
              ┌──────────────────┼──────────────────┐
              │                                     │
   ┌──────────▼──────────┐           ┌──────────────▼──────────┐
   │   Quiz Service      │◄──Feign──►│   Question Service      │
   │   (port 8090)       │           │   (port 8080)           │
   │   PostgreSQL:quizdb │           │   PostgreSQL:questiondb │
   └─────────────────────┘           └─────────────────────────┘
              │                                     │
              └──────────────────┬──────────────────┘
                                 │
                    ┌────────────▼────────────┐
                    │   Service Registry      │
                    │   Eureka (port 8761)    │
                    └─────────────────────────┘
```

### Services

| Service            | Port | Description                                           |
|--------------------|------|-------------------------------------------------------|
| `service-registry` | 8761 | Eureka server — service discovery and registration    |
| `api-gateway`      | 8091 | Spring Cloud Gateway — single entry point for clients |
| `question-service` | 8080 | Manages questions, categories, and scoring            |
| `quiz-service`     | 8090 | Creates quizzes and orchestrates scoring via Feign    |

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.3**
- **Spring Cloud 2025.1.0** (Eureka, OpenFeign, Gateway)
- **PostgreSQL** (separate databases per service)
- **Spring Data JPA / Hibernate**
- **Lombok**
- **Maven**

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+

## Setup

### 1. Create the databases

```sql
CREATE DATABASE questiondb;
CREATE DATABASE quizdb;
```

### 2. Configure environment variables

Each service reads credentials from environment variables with sensible local defaults. For production or to override locally, set:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD=your_secure_password
export DB_URL=jdbc:postgresql://localhost:5432/questiondb   # for question-service
export EUREKA_SERVER=http://localhost:8761/eureka/
```

> **Note:** The defaults (`postgres` / `password`) are for local development only. Always set real credentials in any shared or production environment.

### 3. Start services in order

Services must start in this order so that discovery and routing work correctly:

```bash
# 1. Service Registry
cd service-registry && ./mvnw spring-boot:run

# 2. Question Service
cd question-service && ./mvnw spring-boot:run

# 3. Quiz Service
cd quiz-service && ./mvnw spring-boot:run

# 4. API Gateway
cd api-gateway && ./mvnw spring-boot:run
```

**Seed sample data (first run only):** The file `question-service/src/main/resources/data.sql` contains 30 sample questions (Java, Python, SQL). Run it once directly against your database:

```bash
psql -U postgres -d questiondb -f question-service/src/main/resources/data.sql
```

> `spring.sql.init.mode=never` is set by default to prevent duplicate inserts on every restart.

## API Reference

All requests can go through the API Gateway on port `8091`, which routes to services by name:

- `http://localhost:8091/question-service/...`
- `http://localhost:8091/quiz-service/...`

Or directly to each service.

### Question Service (`/question`)

| Method | Endpoint                    | Description                                      |
|--------|-----------------------------|--------------------------------------------------|
| GET    | `/question/allQuestions`    | Returns all questions                            |
| GET    | `/question/category/{cat}`  | Returns questions filtered by category           |
| POST   | `/question/add`             | Adds a new question                              |
| GET    | `/question/generate`        | Returns random question IDs for quiz generation  |
| POST   | `/question/getQuestions`    | Returns question details by list of IDs          |
| POST   | `/question/getScore`        | Calculates and returns score for submitted answers |

#### Add a question — `POST /question/add`

```json
{
  "questionTitle": "What is the output of System.out.println(1 + 2 + \"3\")?",
  "option1": "123",
  "option2": "33",
  "option3": "6",
  "option4": "Error",
  "rightAnswer": "33",
  "difficultylevel": "Medium",
  "category": "Java"
}
```

#### Generate quiz question IDs — `GET /question/generate?categoryName=Java&numberOfQuestions=5`

Returns a list of random question IDs from the specified category.

### Quiz Service (`/quiz`)

| Method | Endpoint              | Description                          |
|--------|-----------------------|--------------------------------------|
| POST   | `/quiz/create`        | Creates a new quiz                   |
| GET    | `/quiz/get/{id}`      | Returns the questions for a quiz     |
| POST   | `/quiz/submit/{id}`   | Submits answers and returns score    |

#### Create a quiz — `POST /quiz/create`

```json
{
  "title": "Java Basics Quiz",
  "categoryName": "Java",
  "numberOfQuestions": 5
}
```

#### Submit answers — `POST /quiz/submit/{id}`

```json
[
  { "id": 1, "response": "33" },
  { "id": 2, "response": "Compile error" }
]
```

Returns the number of correct answers as an integer.

## Running Tests

```bash
# Run tests for all services
cd question-service && ./mvnw test
cd quiz-service && ./mvnw test
```

## Project Structure

```
microservices-spring-boot/
├── api-gateway/
│   └── src/main/resources/application.properties
├── question-service/
│   └── src/
│       ├── main/
│       │   ├── java/com/maxwell/questionservice/
│       │   │   ├── controller/QuestionController.java
│       │   │   ├── dao/QuestionDao.java
│       │   │   ├── model/         (Question, QuestionWrapper, Response)
│       │   │   └── service/QuestionService.java
│       │   └── resources/
│       │       ├── application.properties
│       │       └── data.sql
│       └── test/
├── quiz-service/
│   └── src/
│       ├── main/java/com/maxwell/quizservice/
│       │   ├── controller/QuizController.java
│       │   ├── dao/QuizDao.java
│       │   ├── feign/QuizInterface.java
│       │   ├── model/         (Quiz, QuizDto, QuestionWrapper, Response)
│       │   └── service/QuizService.java
│       └── test/
└── service-registry/
```
