# RealFreelancer - Quick Start Guide

## 🚀 Get Started in 3 Minutes

### Option 1: One-Click Setup (Recommended)

```bash
# Clone the repository
git clone https://github.com/yourusername/realfreelancer.git
cd realfreelancer

# Run the setup script
./setup.sh
```

This will:
- ✅ Check prerequisites (Java 17+, Node.js 18+, Maven)
- ✅ Set up environment files
- ✅ Install dependencies
- ✅ Start both backend and frontend servers

### Option 2: Manual Setup

#### Backend Setup
```bash
cd backend
cp env.example .env
mvn spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
cp env.example .env
npm install
npm run dev
```

### Option 3: Docker Setup

```bash
# Start all services with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f
```

## 🌐 Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **H2 Database Console**: http://localhost:8080/h2-console

## 🔧 Environment Configuration

### Backend (.env)
```env
JWT_SECRET=your-super-secret-jwt-key-here
DB_URL=jdbc:h2:mem:testdb
SPRING_PROFILES_ACTIVE=dev
```

### Frontend (.env)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## 📱 Features Available

### ✅ Completed (Phase 1)
- User registration and login
- Project listing and filtering
- Basic project cards
- Responsive design
- JWT authentication

### 🚧 Coming Soon (Phase 2)
- Project applications
- Real-time chat
- Reviews and badges
- File uploads
- Advanced filtering

## 🛠️ Development

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

### Database
- **Development**: H2 (in-memory) - no setup required
- **Production**: PostgreSQL - update `.env` with your database credentials

## 🔒 Security Features

- JWT-based authentication
- Input validation and sanitization
- CORS configuration for localhost only
- File size limits (5MB max)
- SQL injection prevention

## 📁 Project Structure

```
realfreelancer/
├── backend/                 # Spring Boot API
│   ├── src/main/java/
│   │   └── com/realfreelancer/
│   │       ├── controller/  # REST endpoints
│   │       ├── model/       # JPA entities
│   │       ├── repository/  # Data access
│   │       ├── service/     # Business logic
│   │       └── config/      # Security & config
│   └── src/main/resources/
│       └── application.yml
├── frontend/                # Next.js React app
│   ├── app/                # Next.js pages
│   ├── components/         # React components
│   ├── hooks/              # Custom hooks
│   └── types/              # TypeScript types
├── docs/                   # Documentation
├── setup.sh               # Quick setup script
└── docker-compose.yml     # Docker setup
```

## 🐛 Troubleshooting

### Common Issues

1. **Port already in use**
   ```bash
   # Kill processes on ports 3000 and 8080
   lsof -ti:3000 | xargs kill -9
   lsof -ti:8080 | xargs kill -9
   ```

2. **Java version issues**
   ```bash
   # Check Java version
   java -version
   # Should be 17 or higher
   ```

3. **Node.js version issues**
   ```bash
   # Check Node.js version
   node --version
   # Should be 18 or higher
   ```

4. **Database connection issues**
   - Check if H2 console is accessible at http://localhost:8080/h2-console
   - Verify database credentials in `.env` file

### Getting Help

- Check the logs: `docker-compose logs` or individual service logs
- Verify environment variables are set correctly
- Ensure all prerequisites are installed
- Check the main README.md for detailed documentation

## 🎯 Next Steps

1. **Explore the API**: Visit http://localhost:8080 to see available endpoints
2. **Create an account**: Register at http://localhost:3000
3. **Post a project**: Use the "Post Project" feature
4. **Customize**: Modify the code to fit your needs

## ⚠️ Important Notes

- This platform is designed for **local development only**
- Do not deploy to production without security modifications
- All data is stored locally (H2 database)
- JWT tokens are stored in localStorage

---

**Happy coding! 🚀** 