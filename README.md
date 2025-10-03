# Innsync: A Cloud-Native SaaS Platform for Hospitality

![Java](https://img.shields.io/badge/Java-17-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen) ![React](https://img.shields.io/badge/React-Vite-blueviolet) ![MySQL](https://img.shields.io/badge/Database-MariaDB-orange)

Innsync is a comprehensive, multi-tenant SaaS platform built with a microservices architecture. It's designed to digitize and streamline operations for small to medium-sized cafes and hotels, providing a full suite of tools from owner onboarding to a complete contactless QR-based ordering and payment flow for customers.

## ✨ Key Features

* **Multi-Tenant Architecture:** Securely serves multiple businesses with a **database-per-tenant** strategy, ensuring complete data isolation through dynamic datasource routing.
* **Contactless Ordering Flow:** A full customer journey from scanning a table's QR code, verifying identity via OTP, viewing the menu, placing an order to a live cart, and requesting the bill.
* **Secure Authentication Service:** A robust, stateless authentication system using **Spring Security & JWTs**. Features include "smart login" (email or phone), role-based access, and a secure forgot/reset password flow.
* **Dynamic Menu Management:** An easy-to-use interface for owners to perform full CRUD operations on their menu categories and items in real-time.
* **AI-Powered Insights (Planned):** A future module to provide business intelligence, such as dynamically ranking food items based on sales data.

## 🏛️ Architecture Overview

This project is structured as a **Monorepo** containing multiple independent **Microservices**. This approach simplifies development while allowing for independent scaling and deployment of services.

### Services
* **`auth-service` (✅ Backend Complete):** Handles owner/staff registration, tenant provisioning, login, and JWT generation.
* **`menu-service` (✅ Backend Complete):** Manages all menu-related CRUD operations for each tenant.
* **`order-service` (✅ Backend Complete):** Orchestrates the entire customer lifecycle, including table management, QR code generation, guest sessions (OTP), and live order tracking.
* **`discovery-service` (Planned):** A Eureka server to allow services to find each other dynamically.
* **`api-gateway` (Planned):** A single entry point for all client requests, routing traffic to the appropriate service.

## 🛠️ Tech Stack

* **Backend:** Java 17, Spring Boot, Spring Security (JWT), Spring Data JPA (Hibernate)
* **Frontend:** React (with Vite), React Router, Axios
* **Database:** MariaDB / MySQL
* **Infrastructure:** Microservices, REST APIs, Monorepo (Git)

## 🚀 Getting Started

### Prerequisites
* Java JDK 17+
* Apache Maven
* Node.js and npm
* A running instance of MariaDB or MySQL.

### Backend Setup
1.  **Create Databases:** Before starting, create the three required databases in your MariaDB/MySQL instance: `registry`, `menu_db`, and `innsync_order_db`.
2.  **Configure:** Ensure the `application.properties` file in each service (`auth-service`, `menu-service`, `order-service`) has the correct database credentials.
3.  **Run Services:** Run the main application class for each of the three backend services. They will start on ports `8080`, `8081`, and `8082` respectively.

### Frontend Setup
1.  Navigate to the frontend directory:
    ```bash
    cd frontend
    ```
2.  Install all necessary dependencies:
    ```bash
    npm install
    ```
3.  Run the development server:
    ```bash
    npm run dev
    ```
4.  The application will be available at `http://localhost:5173`.

## 📄 API Documentation

The complete API documentation, including all available endpoints, request/response examples, and required headers, is maintained as a Postman Collection.
**[Link to your shared Postman Workspace or exported Collection]**

## 👤 Authors

* **[Swapnil Gavali]** - [gavaliswapnil492@gmail.com]
* **[Pratik Londhe]** - [pratiklondhe2004@gmail.com]
