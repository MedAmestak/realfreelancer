# 🚀 RealFreelancer - Full-Stack Freelance Platform

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-14-black.svg)](https://nextjs.org/)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5-blue.svg)](https://www.typescriptlang.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

RealFreelancer is a modern, full-stack freelance platform designed to connect developers with clients. It serves as a comprehensive portfolio project, demonstrating best practices in web development with a powerful Java Spring Boot backend and a dynamic Next.js frontend.

---

<div align="center">
  
**[➡️ Live Demo (Coming Soon)](#)**


</div>

---

## ✨ Key Features

-   **🔐 Secure Authentication**: JWT-based authentication with password encryption and secure session management.
-   **📋 Comprehensive Project Management**: Users can post, browse, and apply for projects with detailed descriptions, budgets, and required skills.
-   **🔍 Advanced Search & Filtering**: A powerful search engine allows users to find projects based on skills, budget, project type, and more.
-   **💬 Real-Time Communication**: WebSocket-powered chat for seamless communication between clients and freelancers (Coming Soon).
-   **🏆 Reputation & Badges**: Users can build their reputation through reviews, ratings, and by earning badges for their achievements (Coming Soon).
-   **📂 File Uploads**: Securely upload and attach files to projects and messages.
-   **🔒 Moderation System**: Tools for reporting and moderating content to maintain a safe platform.
-   **📊 User Dashboards**: Personalized dashboards with analytics on projects, applications, and earnings.
-   **🎨 Modern & Responsive UI**: A clean, intuitive interface built with Tailwind CSS and Framer Motion that works on all devices.

## 🛠️ Tech Stack

| Category      | Technology                                                                                                                                                             |
| :------------ | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Backend**   | **Java 17**, **Spring Boot 3**, Spring Security, Spring Data JPA, JWT                                                                                                  |
| **Frontend**  | **Next.js 14**, **React 18**, **TypeScript**, Tailwind CSS, Framer Motion                                                                                               |
| **Database**  | **PostgreSQL** (Production), **H2** (Development)                                                                                                                      |
| **DevOps**    | **Docker**, **Docker Compose**, GitHub Actions (CI/CD)                                                                                                                 |
| **Build Tools** | **Maven** (Backend), **npm** (Frontend)                                                                                                                                |


## 🚀 Getting Started

This project is fully containerized using Docker, which is the recommended way to run it for a consistent and persistent development experience.

### Prerequisites

-   [Docker](https://www.docker.com/get-started) and [Docker Compose](https://docs.docker.com/compose/install/)
-   [Git](https://git-scm.com/)

### 1. Clone the Repository

```bash
git clone https://github.com/MedAmestak/realfreelancer.git
cd realfreelancer
```

### 2. Configure Your Environment (Crucial Step!)

This project uses a single `.env` file in the root directory to manage all environment variables for all services (frontend, backend, and database).

1.  **Copy the example file**:
    ```bash
    # This command creates your local .env file from the template
    cp .env.example .env
    ```

2.  **Generate a new JWT Secret**: The default JWT secret in the `.env.example` file is insecure. For security, you **must** replace it in your new `.env` file with a unique, strong, and random string.
    -   You can generate a new secret with this command: `openssl rand -hex 32`

3.  **Review and Customize**: Open your new `.env` file. The default values are set up for local development, but you can customize the ports or other variables if needed.

> **Important**: Your `.env` file is already listed in `.gitignore` and will **never** be committed to the repository, keeping your secrets safe.

### 3. Run the Application

With Docker running, start the entire stack (Frontend, Backend, and PostgreSQL Database) with a single command:

```bash
docker-compose up --build -d
```
- `--build`: Builds the images on first run or if you make changes.
- `-d`: Runs the containers in detached mode (in the background).

The application is now running!
-   **Frontend**: [http://localhost:3000](http://localhost:3000)
-   **Backend API**: [http://localhost:8080](http://localhost:8080)
-   **PostgreSQL Database**: Accessible on port `5432` if you use a database tool.

### 4. Stopping the Application

```bash
docker-compose down
```

---

## 💾 Persistent Database

This project now uses a **PostgreSQL** database that runs in a Docker container, ensuring your data is **persistent**.

-   **How it works**: The `docker-compose.yml` file defines a service named `postgres` and a Docker `volume` named `postgres-data`.
-   **Data Persistence**: All database data is stored in the `postgres-data` volume on your local machine. This means your users, projects, and messages will **remain saved** even after you stop and restart the containers with `docker-compose down` and `docker-compose up`.
-   **Viewing the Data**: You can connect to the database using any standard database client (like DBeaver, TablePlus, or pgAdmin) with these credentials:
    -   **Host**: `localhost`
    -   **Port**: `5432`
    -   **Database**: `realfreelancer`
    -   **Username**: `user`
    -   **Password**: `password`

## 🤝 Project Structure

The repository is organized into two main parts: `backend` and `frontend`, with clear separation of concerns.

```
realfreelancer/
├── backend/            # Spring Boot API
│   ├── src/main/java/com/realfreelancer/
│   │   ├── config/     # Security & App Configuration
│   │   ├── controller/ # REST API Endpoints
│   │   ├── dto/        # Data Transfer Objects
│   │   ├── model/      # JPA Entities
│   │   ├── repository/ # Data Access Layer
│   │   └── service/    # Business Logic
│   ├── pom.xml         # Maven Dependencies
│   └── Dockerfile      # Backend Container
├── frontend/           # Next.js React App
│   ├── app/            # Next.js 14 App Directory & Pages
│   ├── components/     # Reusable React Components
│   ├── contexts/       # Global State Management (e.g., Auth)
│   ├── hooks/          # Custom React Hooks
│   ├── types/          # TypeScript Type Definitions
│   └── Dockerfile      # Frontend Container
├── .github/workflows/  # CI/CD Workflows
├── docker-compose.yml  # Docker Orchestration
└── README.md           # This file
```

## 🤝 Contributing

Contributions are welcome! If you have suggestions for improvement or want to add new features, please feel free to:
1.  Fork the repository.
2.  Create a new feature branch (`git checkout -b feature/your-amazing-feature`).
3.  Commit your changes (`git commit -m 'Add some amazing feature'`).
4.  Push to the branch (`git push origin feature/your-amazing-feature`).
5.  Open a Pull Request.

## 📝 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.

## 📞 Contact

Mohamed Amestak - [GitHub @MedAmestak](https://github.com/MedAmestak)

---
<p align="center">
  ⭐ Star this repository if you find it helpful! ⭐
</p>
