# PeakyPicks Backend

PeakyPicks is a RESTful API built with Spring Boot that allows a user to search for a place or service in Google and selects only the top 5 results using a custom algorithm. It provides user authentication and enables users to save their location-based picks. It integrates JWT authentication for security and utilizes Hibernate for database interactions.

## Features

- **User Authentication** (Register/Login with JWT)
- **Secure Pick Management** (Add, Retrieve, Delete Picks)
- **Role-Based Access Control** (Spring Security)
- **CORS Configured** (Supports frontend integration)
- **Stateless Authentication** (JWT + Spring Security)
- **Hibernate + JPA** (Database interactions)

## Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **Spring Data JPA (Hibernate)**
- **H2/PostgreSQL/MySQL (Configurable)**
- **Maven**
- **Swagger (API Documentation)**
- **Lombok (Simplified Annotations)**

---

## Installation & Setup

### Clone the Repository

```sh
git clone https://github.com/ierkyr/peakypicks.git
cd peaky-picks-backend
