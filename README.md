# Innsync Platform

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)

A multi-tenant SaaS platform designed for small to medium-sized hotels and cafes. Innsync provides modern digital tools to streamline operations, including QR-based table ordering, digital menu management, and integrated billing.

## ✨ Features

* **Multi-Tenant Architecture:** Securely serves multiple businesses with a dedicated database for each tenant.
* **Dynamic Onboarding:** Seamless registration for hotel/cafe owners with automatic provisioning of their dedicated environment.
* **Role-Based Access Control:** Pre-defined roles for Owner, Manager, and Staff to manage permissions effectively.
* **QR-Based Table Ordering:** Allows customers to view the menu and place orders directly from their table by scanning a QR code.
* **Digital Menu Management:** Easy-to-use interface for owners to create, update, and manage their menu items and categories.
* **Order & Billing Management:** Centralized dashboard to track live orders and generate digital bills.

## 🛠️ Tech Stack

### Backend
* **Java 17+**
* **Spring Boot 3.x**
* **Spring Security:** For authentication and authorization (completed).
* **Spring Data JPA / Hibernate:** For database interaction and ORM.
* **MySQL / MariaDB:** Relational database for storing platform and tenant data.
* **Maven:** For dependency management and build automation.
* **Lombok:** To reduce boilerplate code.

### Frontend (Planned)
* **React.js**
* **Axios:** For making API requests.
* **State Management (e.g., Redux Toolkit)**

## 🏛️ Architecture Overview

This project is structured as a **Monorepo** containing multiple independent **Microservices**. This approach simplifies development and code sharing while allowing for independent scaling of services.

### Services

* **`auth-service` (completed):** Handles owner registration, tenant provisioning, user login, and role management.
* **`menu-service` (completed):** Manages all menu-related operations for each tenant.
* **`order-service` (completed):** Manages the entire lifecycle of a customer's order.
* **`api-gateway` (Planned):** Single entry point for all client requests, routing traffic to the appropriate service.


## 👤 Author

* **[Swapnil Gavali]** - [gavaliswapnil492@gmail.com]
* **[Pratik Londhe]** - [pratiklondhe2004@gmail.com]


### Frontend (Now Added 🚀)

The frontend for Innsync is built with **React + Vite** and lives in the `frontend/` folder of this repository.

#### ⚡ Setup Instructions

1. **Navigate to the frontend folder**
   ```bash
   cd frontend
Install dependencies

bash
Copy code
npm install
Run the development server

bash
Copy code
npm run dev
By default, the app runs at http://localhost:5173.

Build for production

bash
Copy code
npm run build
