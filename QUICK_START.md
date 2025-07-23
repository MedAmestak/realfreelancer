# ⚡ RealFreelancer - Quick Start Guide

## 🚀 Get Started in 3 Minutes

### Option 1: One-Click Setup (Recommended)

```bash
git clone https://github.com/MedAmestak/realfreelancer.git
cd realfreelancer
./setup.sh
```

This will:
- ✅ Check prerequisites (Java 17+, Node.js 18+, Maven)
- ✅ Set up environment files
- ✅ Install dependencies
- ✅ Start both backend and frontend servers

---

### Option 2: Manual Setup

#### Backend Setup
```bash
cd backend
cp .env.example .env
mvn spring-boot:run
```

#### Frontend Setup
```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

---

### Option 3: Docker Setup

```bash
docker-compose up -d
# View logs
docker-compose logs -f
```

---

## 🌐 Access the Application

- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **PostgreSQL Database**: localhost:5432

---

## 🔧 Environment Configuration

### Backend (.env)
```env
JWT_SECRET=YOUR_JWT_SECRET
DB_URL=jdbc:h2:mem:testdb
SPRING_PROFILES_ACTIVE=dev
```
### Frontend (.env)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

## 📱 Features Available

### ✅ Completed
- User registration and login
- Project listing, filtering, and applications
- Real-time chat (beta)
- Basic project cards
- Responsive design
- JWT authentication

### 🚧 Coming Soon
- Reviews and badges
- File uploads
- Advanced filtering

---

## 🛠️ Development

### Backend
```bash
cd backend
mvn spring-boot:run
```
### Frontend
```bash
cd frontend
npm run dev
```

---

## 🐞 Troubleshooting

> **Common Issues:**

- **Port already in use**
  ```bash
  lsof -ti:3000 | xargs kill -9
  lsof -ti:8080 | xargs kill -9
  ```
- **Java version issues**
  ```bash
  java -version # Should be 17 or higher
  ```
- **Node.js version issues**
  ```bash
  node --version # Should be 18 or higher
  ```
- **Database connection issues**
  - Check your `.env` credentials

> For more help, see [docs/README.md](./docs/README.md) or open an issue.

---

## 🎯 What's Next?

1. **Explore the API**: Visit http://localhost:8080
2. **Create an account**: Register at http://localhost:3000
3. **Post a project**: Try the "Post Project" feature
4. **Contribute**: See [Contributing](https://github.com/MedAmestak/realfreelancer/issues) or open a PR!

---

## ⚠️ Important Notes

- This platform is for **local development only**
- Do not deploy to production without security modifications
- All data is stored locally (H2 database by default)
- JWT tokens are stored in localStorage

---

**Happy coding! 🚀** 