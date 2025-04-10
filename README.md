# 👥 Kitchensink - Member Management System

A secure, modern member registration and management app built with:

- 🧠 Spring Boot 3.x
- 🔐 Google OAuth2 OIDC + JWT
- 🍃 Thymeleaf + JavaScript (dynamic UI)
- 💾 MongoDB
- 🔒 Role-based access for UI & API (`ADMIN`, `USER`)

---

## 🌐 Tech Stack

| Layer         | Technology                                 |
|---------------|---------------------------------------------|
| Backend       | Spring Boot 3.x, Spring Security OIDC       |
| Auth          | Google OAuth2 / OIDC, JWT                   |
| Frontend      | Thymeleaf, Vanilla JS                       |
| Database      | MongoDB (Dev: Embedded or Docker)           |
| Testing       | JUnit 5, MockMvc, Embedded Mongo            |
| Build Tool    | Maven                                       |

---

## ✅ Features

- Google Login via OAuth2 (OIDC)
- Dual Security:
  - Session-based login for UI
  - JWT-based stateless access for API
- Admin dashboard to view, promote, demote or delete members
- Embedded MongoDB support for dev/test
- Thymeleaf views powered by dynamic JS calls
- Token endpoint exposed for UI to fetch JWT

---

## 🚀 Getting Started

### 1. Clone the repo

```bash
git clone https://github.com/your-org/kitchensink.git
cd kitchensink
```

### 2. Google OAuth Setup
Visit: Google Cloud Console

Go to Credentials > Create OAuth 2.0 Client ID

Set redirect URI to:
http://localhost:8080/login/oauth2/code/google

### 3. Update application.properties
spring.data.mongodb.uri=YOUR_MONGO_DB_CONNECTION_STRING
spring.security.oauth2.client.registration.google.client-id=YOUR_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_CLIENT_SECRET

### 4. Create your own Mongo DB cluster on cloud or deploy docker image

- Go to https://cloud.mongodb.com/
- Sign up
- Create cluster
- Note down connection string
- Use any supported tool to connect to cluster and create database

OR

##### Start MongoDB via Docker
``` bash
docker run -d -p 27017:27017 --name mongo mongo
```

### 5. Build the app
``` bash
cd <project location>/kitchensink
mvn clean install
```

### 6. Access the app
http://localhost:8080

### 🔐 Security Design
✅ Session login (/admin/**) – OIDC via Spring Security

✅ API protected by Authorization: Bearer <JWT> – validated via Google

✅ Dual SecurityFilterChain: UI (session) & API (stateless JWT)

✅ Token endpoint fetches user's JWT post-login

### 🧪 Running Tests
``` bash
cd <project location>/kitchensink
mvn clean test
```
