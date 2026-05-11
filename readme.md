# Quantity Measurement App (Microservices Architecture)

A robust, full-stack microservices application built with Spring Boot and React. This project demonstrates a scalable cloud architecture featuring dynamic service discovery, centralized API routing, secure JWT authentication paired with Google OAuth2, and a modern frontend.

---

## 🚀 Live Production Links

The application is fully containerized and deployed on Render's cloud infrastructure.

| Service | Live URL | Description |
| :--- | :--- | :--- |
| **Frontend (React)** | [qma-app.onrender.com](https://quantity-measurement-app-qma.onrender.com) | The main user interface. |
| **API Gateway** | [api-gateway.onrender.com](https://quantity-measurement-app-api-gateway.onrender.com) | Central entry point and CORS handler. |
| **Auth Service** | [auth-service.onrender.com](https://quantity-measurement-app-auth-service.onrender.com) | Handles Google OAuth2 & JWT generation. |
| **Quantity Service** | [quantity-service.onrender.com](https://quantity-measurement-app-quantity-service.onrender.com) | Core business logic and database operations. |
| **Eureka Server** | [eureka-service.onrender.com](https://quantity-measurement-app-44gd.onrender.com) | Service registry for dynamic routing. |

---

## 📚 API Documentation (Swagger UI)

Interactive REST API documentation is generated automatically using OpenAPI/Springdoc. You can test the live endpoints directly through the browser:

* **Auth Service API:** [Swagger UI Link](https://quantity-measurement-app-auth-service.onrender.com/swagger-ui/index.html)
* **Quantity Service API:** [Swagger UI Link](https://quantity-measurement-app-quantity-service.onrender.com/swagger-ui/index.html)

---

## 🛠️ Tech Stack

**Backend:**
* **Java 21** & **Spring Boot 3.x**
* **Spring Cloud Gateway:** API routing and edge security.
* **Spring Cloud Netflix Eureka:** Service discovery and registration.
* **Spring Security & OAuth2:** Google login integration and stateless JWT authentication.
* **Spring Data JPA / Hibernate:** ORM and database management.
* **MySQL:** Managed cloud database (Aiven).

**Frontend:**
* **React.js** (Bootstrapped with Vite)
* **React Router:** For frontend navigation and OAuth callback handling.

**DevOps & Deployment:**
* **Docker:** Containerization for all backend services.
* **Render:** Cloud hosting for web services and static sites.

---

## 🏗️ System Architecture

1.  **Client Request:** The user interacts with the React frontend.
2.  **API Gateway:** All backend requests pass through the Gateway, which resolves CORS and securely routes traffic.
3.  **Service Discovery:** The Gateway consults the Eureka Server to find the internal IP/port of the requested microservice.
4.  **Authentication:** * Users log in via Google OAuth2.
    * The Auth Service issues a custom JWT.
    * Subsequent requests to protected routes (like the Quantity Service) require this JWT in the Authorization header.
5.  **Database:** Services communicate with a centralized MySQL database hosted on Aiven.

---

## 💻 Local Development Setup

To run this microservices cluster on your local machine:

### Prerequisites
* Java 21 installed
* Node.js & npm installed
* Docker & Docker Compose (optional, for containerized local testing)
* A local or cloud MySQL database

### Environment Variables
You must create a `.env` file or provide system environment variables for the backend services. *Never commit these secrets to version control!*

```env
# Database
SPRING_DATASOURCE_URL=jdbc:mysql://YOUR_DB_HOST:3306/qma_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Security
JWT_SECRET=your_super_secret_256_bit_string
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret

# Routing
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/
FRONTEND_URL=http://localhost:5173