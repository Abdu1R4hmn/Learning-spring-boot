# Web Security with Spring Boot Security

This repository serves as a hands-on learning project for gradually mastering web security using Spring Boot Security. Each folder represents a step in the learning process, starting with fundamentals and introducing more advanced security concepts.

---

## Table of Contents
1. [Folder 1: Basic Authentication with In-Memory Users](#folder-1-basic-authentication-with-in-memory-users)
2. [Folder 2: DAO-Based Authentication with Roles and Registration](#folder-2-dao-based-authentication-with-roles-and-registration)
3. [Folder 3: DTO Validation for Secure Login](#folder-3-dto-validation-for-secure-login)
4. [Folder 4: Custom Exception Handling](#folder-4-custom-exception-handling)
5. [Folder 5: Frontend Integration](#folder-5-frontend-integration)

---

## Folder 1: Basic Authentication with In-Memory Users

### Overview
In this stage, security is implemented using **Basic Authentication (AuthN)** and **Authorization (AuthZ)**. All users and credentials are stored in memory, meaning they are hardcoded directly in the application.

This is an important starting point because it demonstrates the fundamental building blocks of authentication and authorization without adding complexity.

### Features
- **Basic Authentication**:
  - Clients send their username and password as a `Base64` string in the `Authorization` header.
  - Spring Security validates the credentials. If correct, access is permitted.
- **Role-Based Authorization**:
  - Public access to endpoints like `/api/auth/public`.
  - Secured endpoints like `/api/authenticated/admin` require specific roles.

### Why is this important?
- **What does this solve?**
  - It enforces that users must authenticate themselves before accessing protected resources (e.g., an admin-only endpoint).
- **What does this protect against?**
  - Prevents unauthorized users from accessing sensitive data (e.g., admin-only features).

### Graph: Basic Authentication Flow

<img width="784" height="592" alt="image" src="https://github.com/user-attachments/assets/53786107-4053-4671-99c3-aeb0460f74db" />

---

## Folder 2: DAO-Based Authentication with Roles and Registration

### Overview
This stage replaces in-memory storage with a **Database Access Object (DAO)**. The application now connects to a database to fetch and validate user credentials. Users can also register dynamically through an API.

### Features
- **Database-Backed Authentication**: User credentials are securely validated against a database.
- **Dynamic Registration**: Users can register themselves via `/api/register`. Their credentials are then stored in the database.
- **Role-Based Access Control**:
  - Roles like `ADMIN` and `USER` are stored in the database and dynamically applied when authenticating users.

### Why is this important?
- **What does this solve?**
  - Provides persistent storage for users and roles, making the system scalable and production-ready.
- **What does this protect against?**
  - Hardcoding credentials directly in the application is unsafe for real-world use. Storing credentials in a database ensures data consistency and security.

### Graph: DAO Authentication Flow
<img width="685" height="385" alt="image" src="https://github.com/user-attachments/assets/89bc5ebb-c227-4b6b-a136-f4cb4a450abc" />

---

## Folder 3: DTO Validation for Secure Login

### Overview
In this stage, we introduce **Data Transfer Objects (DTOs)** to encapsulate and validate API inputs. DTOs play a critical role in ensuring that user-provided data is sanitized and validated before being processed by the backend.

### Features
- **Input Validation**: Ensures that user inputs (like username and password) meet specific criteria (e.g., length, format).
- **Clean Separation**: Encapsulates client-facing objects (DTOs) separately from database or internal logic.

### Why is this important?
- **What does this solve?**
  - Prevents invalid or malicious data from entering the system.
- **What does this protect against?**
  - **Injection Attacks**: Protects against SQL injection and other code injection attacks by validating inputs at the boundary.
  - **Improper Input Handling**: Prevents oversized or malformed requests from reaching the business logic.

### Graph: DTO Validation Flow
<img width="585" height="326" alt="image" src="https://github.com/user-attachments/assets/32ae23ea-8870-451f-81f3-6db3884fb6b0" />

---

## Folder 4: Custom Exception Handling

### Overview
Custom exception handling provides user-friendly error messages when something goes wrong. Instead of returning generic HTTP errors, the system handles exceptions gracefully and provides clear responses.

### Features
- **Global Exception Handling**: All exceptions are caught and handled centrally.
- **Custom Exceptions**: Define and use specific exceptions for common scenarios (e.g., UserNotFoundException, InvalidCredentialsException).
- **Detailed API Responses**: Exceptions are converted into well-structured responses to help clients understand the issue.

### Why is this important?
- **What does this solve?**
  - Simplifies error handling for developers and provides a unified mechanism to capture and process errors.
- **What does this protect against?**
  - Prevents leaking stack traces or unnecessary technical details to end-users, ensuring better security and usability.

### Graph: Exception Handling Flow

```
{
"status": 400,
"message": "Validation failed",
"errors": [
"Email must be valid",
"Password must contain at least one number"
],
"timestamp": "2025-01-01T10:15:30"
}
```

---

## Folder 5: Frontend Integration

### Overview
In this stage, the frontend is integrated to demonstrate full-stack communication between the UI and the backend. The frontend interacts with the backend securely and handles tokens for authenticated APIs.

### Features
- **CORS Configuration**: Ensures that the frontend and backend can interact securely across origins.
- **Token Authentication**: Token-based APIs are easier to consume and enhance security in stateless applications.

---

## Conclusion
This repository provides a clear roadmap for learning Spring Boot Security step by step. By understanding the **what**, the **why**, and the **protection it provides**, you gain confidence in implementing real-world security measures.
