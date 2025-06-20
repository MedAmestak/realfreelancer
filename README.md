# 🚀 RealFreelancer - Full-Stack Freelance Platform

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Next.js](https://img.shields.io/badge/Next.js-14.0.0-black.svg)](https://nextjs.org/)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.2.0-blue.svg)](https://www.typescriptlang.org/)
[![Tailwind CSS](https://img.shields.io/badge/Tailwind%20CSS-3.3.5-38B2AC.svg)](https://tailwindcss.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-3.8+-2496ED.svg)](https://www.docker.com/)

> **A modern, full-stack freelance platform built with Spring Boot and Next.js**  
> Perfect for developers to showcase their skills and collaborate on portfolio-building projects.

## 🌟 Features

### 🔐 Authentication & Security
- **JWT-based authentication** with secure token management
- **Password encryption** using BCrypt
- **Role-based access control** with Spring Security
- **Input validation** and sanitization
- **CORS configuration** for secure cross-origin requests

### 📋 Project Management
- **Create and manage projects** with detailed descriptions
- **Skill-based project matching** with advanced filtering
- **Project applications** with proposals and budgets
- **Real-time project status** tracking
- **File attachments** support (5MB max)

### 💬 Communication
- **Real-time chat system** between clients and freelancers
- **Message history** with read/unread status
- **File sharing** in conversations
- **Project-specific chat rooms**

### 🏆 Reputation System
- **Review and rating system** (1-5 stars)
- **Badge system** for achievements
- **Reputation points** based on performance
- **Portfolio integration** with GitHub links

### 🎨 Modern UI/UX
- **Responsive design** for all devices
- **Dark/light mode** support
- **Smooth animations** with Framer Motion
- **Modern components** with Tailwind CSS
- **Intuitive navigation** and user experience

## 🛠️ Tech Stack

### Backend
- **Java 17** - Modern Java features and performance
- **Spring Boot 3.2.0** - Rapid application development
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **PostgreSQL/H2** - Database (production/development)
- **JWT** - Stateless authentication
- **Maven** - Dependency management

### Frontend
- **Next.js 14** - React framework with SSR/SSG
- **React 18** - Modern React with hooks
- **TypeScript** - Type-safe JavaScript
- **Tailwind CSS** - Utility-first CSS framework
- **Framer Motion** - Animation library
- **React Hook Form** - Form management
- **Zustand** - State management

### DevOps
- **Docker** - Containerization
- **Docker Compose** - Multi-container orchestration
- **GitHub Actions** - CI/CD (ready to implement)

## 📁 Project Structure

```
realfreelancer/
├── backend/                          # Spring Boot API
│   ├── src/main/java/com/realfreelancer/
│   │   ├── controller/               # REST API endpoints
│   │   ├── model/                    # JPA entities
│   │   ├── repository/               # Data access layer
│   │   ├── service/                  # Business logic
│   │   ├── config/                   # Security & configuration
│   │   └── dto/                      # Data transfer objects
│   ├── src/main/resources/
│   │   └── application.yml           # Application configuration
│   ├── pom.xml                       # Maven dependencies
│   └── Dockerfile                    # Backend container
├── frontend/                         # Next.js React app
│   ├── app/                          # Next.js 14 app directory
│   ├── components/                   # Reusable React components
│   ├── hooks/                        # Custom React hooks
│   ├── types/                        # TypeScript type definitions
│   ├── package.json                  # Node.js dependencies
│   └── Dockerfile                    # Frontend container
├── docs/                             # Documentation
├── docker-compose.yml               # Multi-container setup
├── setup.sh                         # One-click setup script
└── README.md                        # This file
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18 or higher
- Maven 3.6+
- Docker (optional)

### Option 1: One-Click Setup
```bash
git clone https://github.com/MedAmestak/realfreelancer.git
cd realfreelancer
chmod +x setup.sh
./setup.sh
```

### Option 2: Manual Setup

#### Backend
```bash
cd backend
cp env.example .env
mvn spring-boot:run
```

#### Frontend
```bash
cd frontend
cp env.example .env
npm install
npm run dev
```

### Option 3: Docker Setup
```bash
docker-compose up -d
```

## 🌐 Access Points

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
- **API Documentation**: Available at `/api` endpoints

## 🔧 Configuration

### Environment Variables

#### Backend (.env)
```env
JWT_SECRET=YOUR_JWT_SECRET
DB_URL=jdbc:h2:mem:testdb
DB_USERNAME=sa
DB_PASSWORD=
SPRING_PROFILES_ACTIVE=dev
```

#### Frontend (.env)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## 📚 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile
- `GET /api/auth/validate` - Validate JWT token

### Projects
- `GET /api/projects` - List all projects
- `POST /api/projects` - Create new project
- `GET /api/projects/{id}` - Get project details
- `POST /api/projects/{id}/apply` - Apply for project

### Chat
- `GET /api/chat/{projectId}` - Get chat messages
- `POST /api/chat/{projectId}` - Send message

### Reviews & Badges
- `POST /api/reviews` - Submit review
- `GET /api/users/{id}/badges` - Get user badges

## 🎯 Key Features Implemented

### ✅ Phase 1 (Completed)
- [x] User authentication with JWT
- [x] Project creation and management
- [x] Advanced filtering and search
- [x] Responsive UI with modern design
- [x] Real-time project status updates
- [x] File upload support
- [x] Security best practices

### ✅ Phase 2 (Completed)
- [ ] Real-time chat implementation
- [ ] Review and rating system
- [ ] Badge and reputation system
- [ ] Advanced project matching
- [ ] Email notifications

### ✅ Phase 3 (Completed)
- [ ] Advanced Search & Analytics
- [ ] Dashboard & Analytics
- [ ] Notifications System
- [ ] Moderation & Reporting
- [ ] Enhanced Frontend Components

### (FULL APP NOT COMPlETED YET)

## 🔒 Security Features

- **JWT Authentication** - Secure token-based authentication
- **Password Encryption** - BCrypt hashing for passwords
- **Input Validation** - Comprehensive validation on all inputs
- **SQL Injection Prevention** - Parameterized queries
- **XSS Protection** - Content Security Policy
- **CORS Configuration** - Controlled cross-origin access
- **File Upload Security** - Size and type restrictions

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 📊 Performance

- **Backend**: Spring Boot with optimized JPA queries
- **Frontend**: Next.js with code splitting and optimization
- **Database**: Indexed queries for fast retrieval
- **Caching**: Ready for Redis integration

## 🚀 Deployment

### Docker Deployment
```bash
docker-compose up -d
```

### Manual Deployment
1. Build backend: `mvn clean package`
2. Build frontend: `npm run build`
3. Deploy to your preferred hosting service

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Spring Boot** - For the robust backend framework
- **Next.js** - For the modern React framework
- **Tailwind CSS** - For the utility-first CSS framework
- **Framer Motion** - For the smooth animations
- **Lucide React** - For the beautiful icons

## 📞 Contact

- **GitHub**: [@MedAmestak](https://github.com/MedAmestak)
- **LinkedIn**: [Mohamed Amestak](https://www.linkedin.com/in/mohamed-amestak-3b9153195/)

---

<div align="center">

**⭐ Star this repository if you found it helpful!**

**Built with ❤️ for the developer community**

</div> 
