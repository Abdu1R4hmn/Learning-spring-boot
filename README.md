# Web Security with Spring Boot Security

This repository serves as my personal learning project to deeply understand web security using Spring Boot Security. Instead of trying to learn everything at once, I broke down the implementation into smaller, manageable pieces—each focusing on a specific aspect of security.

This gradual and iterative approach has helped me clarify how each feature works in isolation and how they all fit together to create a secure application.

---

## Introduction

Building secure web applications can seem overwhelming due to the sheer number of concepts and features to understand. Concepts like authentication, authorization, role management, data validation, exception handling, and frontend integration each add their own layers of complexity. To avoid confusion and make the learning process manageable, I created this repository to **dissect security piece by piece**.

### Why this structure?
Instead of tackling everything at once, I focused on understanding **one concept at a time**. This repository is an exploration of Spring Boot Security, where:
1. Each **folder** focuses on introducing a single feature or concept.
2. The progression from one folder to the next shows how the pieces come together incrementally.
3. Explaining each feature to myself helped me **reinforce my understanding**, identify gaps, and build a foundation for secure application development.

### My Learning Process
This repository serves as a record of **how I approached learning Spring Boot Security**:
1. Start with **basic authentication** using in-memory user storage.
2. Gradually add real-world features like database-backed authentication, role-based access control, and user registration.
3. Dive into details like validating user input with Data Transfer Objects (DTOs) to ensure data integrity.
4. Build centralized error handling to deliver user-friendly messages while securing internal application details.
5. Finally, integrate everything with a frontend, simulating a secure full-stack application.

Each folder is intentionally small in scope, to focus entirely on one piece of the puzzle. By limiting the scope, I can fully understand each building block individually before combining them into a complete solution.

This repository reflects how I approached learning Spring Boot Security at my own pace, emphasizing iterative progress and clarity.

---

## Table of Contents
1. [Folder 1: Basic Authentication with In-Memory Users](#folder-1-basic-authentication-with-in-memory-users)
2. [Folder 2: DAO-Based Authentication with Roles and Registration](#folder-2-dao-based-authentication-with-roles-and-registration)
3. [Folder 3: DTO Validation for Secure Login](#folder-3-dto-validation-for-secure-login)
4. [Folder 4: Custom Exception Handling](#folder-4-custom-exception-handling)

---

## Folder 1: Basic Authentication with In-Memory Users

### Overview
In this initial stage, I implemented **Basic Authentication (AuthN)** and **Authorization (AuthZ)**. Here, user credentials are stored directly in memory, which made it easier to get started and focus purely on the mechanics of authentication and authorization.

This stage gave me a clear understanding of how user authentication is set up in Spring Security and the role of the `SecurityFilterChain` in validating authentication and enforcing access rules.

### Features
- **Basic Authentication**:
  - Simple username/password authentication using an `Authorization` header.
- **Role-Based Authorization**:
  - Public versus secured endpoints:
    - Everyone can access `/api/auth/public`.
    - Secured resources like `/api/authenticated/admin` require specific roles.

### Why is this important?
- **Understanding Authentication Flow**:
  - Learned how user credentials are passed and validated by Spring Security.
- **Understanding Authorization Rules**:
  - Practiced enforcing rules like "any authenticated user" or "only admins allowed" for different endpoints.

### What did this solve for me?
- It helped me visualize what happens **under-the-hood** when a user logs in:
  - How credentials are extracted from an HTTP request.
  - How they’re validated against an in-memory store.
  - The mechanism to enforce role-based restrictions on endpoints.

### Graph: Basic Authentication Flow

<img width="784" height="592" alt="image" src="https://github.com/user-attachments/assets/e1606426-233a-46a6-86d8-6aa27f8b3985" />



---

## Folder 2: DAO-Based Authentication with Roles and Registration

### Overview
At this stage, I moved from in-memory user storage to a **Database Access Object (DAO)** for persistence. Adding a database allowed me to understand how authentication scales with user data.

I also implemented dynamic **user registration**, which involves creating and storing user data (username, password, roles) in a persistent database.

### Features
- **Persistent User Storage**:
  - Instead of hardcoding users in memory, credentials are stored in a database table (`Users`) and fetched using DAOs.
- **Role Assignment**:
  - Users are assigned roles (e.g., `ADMIN`, `USER`) that are dynamically checked during authentication.
- **Dynamic Registration**:
  - Users can register themselves at runtime through `/api/register`.

### Why is this important?
- **Learning Persistence**:
  - Helped me understand how to make user authentication scalable and production-ready by integrating a database.
- **Role Management**:
  - Explored how user roles are stored and validated dynamically during login.

### What did this solve for me?
- Made me understand why an in-memory store is insufficient for real-world applications.
- Allowed me to build features like registration dynamically—without restarting the app.

### Graph: DAO Authentication Flow

<img width="685" height="385" alt="image" src="https://github.com/user-attachments/assets/428acdd5-fc2e-4573-8028-6275dbe64870" />

---
## Folder 3: DTO Validation for Secure Login

### Overview
In this stage, I focused on using **Data Transfer Objects (DTOs)** to encapsulate and validate user inputs. DTOs protect the backend from malicious, malformed, or oversized requests by performing validation at the API boundary.

### Features
- **Input Validation**:
  - Ensures all API inputs (e.g., username, password) meet defined criteria (e.g., length, required fields, format).
- **Data Separation**:
  - DTOs isolate client-side data from internal database logic.

### Why is this important?
- **Preventing Bad Data**:
  - Ensures the backend only processes valid and clean data.
- **Strengthening Security**:
  - Protects against injection attacks and malformed API requests.

### What did this solve for me?
- Made it clear how input validation works hand-in-hand with API design.
- Allowed me to explore `@Valid` and other validation annotations provided by Spring.

### Graph: DTO Validation Flow

<img width="585" height="326" alt="image" src="https://github.com/user-attachments/assets/2c5e7f87-3240-4262-a445-9d05941f5d04" />

---

## Folder 4: Custom Exception Handling

### Overview
At this stage, I created a centralized exception-handling mechanism to simplify error management. This ensures consistent error responses for all API endpoints regardless of where the error occurs in the application. By using a global exception handler, I learned how to control error structures and how to hide sensitive error information from users.

### Features
- **Global Exception Handling**:
  - All exceptions are caught and handled in a centralized class.
  - Specific exceptions, such as `UserNotFoundException` or `InvalidCredentialsException`, are mapped to custom responses (e.g., HTTP 404, HTTP 401).
- **Detailed Error Responses**:
  - Exception messages are mapped to user-friendly and consistent JSON responses.
- **Error Logging**:
  - Exceptions are logged for debugging purposes, instead of exposing unnecessary details to the client.

### Why is this important?
- **Simplification**:
  - Centralized exception handling avoids duplicating error-handling code in multiple controllers.
- **Security**:
  - Helps ensure sensitive information (e.g., stack traces, database errors) is not leaked to clients, which could otherwise expose vulnerabilities.

### What did this solve for me?
- It made me aware of the importance of consistent error handling.
- Showed how to handle REST API exceptions while preventing insecure data leaks.

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
