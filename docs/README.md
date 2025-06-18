# RealFreelancer

A free freelance platform for portfolio-building projects. **Local-only use (localhost)** - designed for developers to showcase their skills and collaborate on open-source projects.

## 🚨 Important Notice

**This platform is designed for local use only. Not for public deployment without significant security modifications.**

## Features

- **Project Management**: Post and apply for free app projects
- **Real-time Chat**: Communicate between clients and freelancers
- **Reputation System**: Earn badges and reviews (1-5 stars)
- **File Sharing**: Upload project files (5MB max)
- **Skill-based Matching**: Filter projects by required skills
- **Portfolio Integration**: Share your profile link on GitHub

## Tech Stack

- **Backend**: Spring Boot 3.x (Java 17+)
- **Frontend**: Next.js 14.x (React)
- **Database**: H2 (development), PostgreSQL (production)
- **Styling**: Tailwind CSS
- **Authentication**: JWT tokens
- **File Storage**: Base64 in DB (local) or Cloudinary (optional)

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL (optional, H2 works for development)
- Maven

## Quick Setup

### 1. Clone the Repository

```bash
git clone https://github.com/MedAmestak/realfreelancer.git
cd realfreelancer
```

### 2. Backend Setup

```bash
cd backend
cp .env.example .env
# Edit .env with your configuration
mvn spring-boot:run
```

The backend will run on `http://localhost:8080`

### 3. Frontend Setup

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

The frontend will run on `http://localhost:3000`

## Environment Variables

### Backend (.env)
```env
JWT_SECRET=your-super-secret-jwt-key-here
DB_URL=jdbc:h2:mem:testdb
DB_USERNAME=sa
DB_PASSWORD=
SPRING_PROFILES_ACTIVE=dev
```

### Frontend (.env)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## Database Setup

### Development (H2 - In-Memory)
The application uses H2 by default for easy development. No setup required.

### Production (PostgreSQL)
1. Install PostgreSQL
2. Create a database
3. Update `backend/.env`:
```env
DB_URL=jdbc:postgresql://localhost:5432/realfreelancer
DB_USERNAME=your_username
DB_PASSWORD=your_password
SPRING_PROFILES_ACTIVE=prod
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile

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

## Project Structure

```
realfreelancer/
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/realfreelancer/
│   │       ├── controller/  # REST endpoints
│   │       ├── model/       # JPA entities
│   │       ├── repository/  # Data access layer
│   │       ├── service/     # Business logic
│   │       └── config/      # Security & configuration
│   └── src/main/resources/
│       └── application.yml
├── frontend/                # Next.js application
│   ├── pages/              # Next.js pages
│   ├── components/         # React components
│   ├── styles/             # Tailwind CSS
│   └── public/             # Static assets
└── docs/                   # Documentation
```

## Development

### Backend Development
```bash
cd backend
mvn spring-boot:run
```

### Frontend Development
```bash
cd frontend
npm run dev
```

### Running Tests
```bash
# Backend tests
cd backend
mvn test

# Frontend tests
cd frontend
npm test
```

## Security Features

- JWT-based authentication
- Input validation and sanitization
- File size limits (5MB max)
- CORS configuration for localhost only
- SQL injection prevention
- XSS protection

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test locally
5. Submit a pull request

## License

MIT License - see LICENSE file for details

## Support

For issues and questions, please open an issue on GitHub.

---

**Remember**: This platform is designed for local development and portfolio building. Do not deploy to production without implementing additional security measures. 