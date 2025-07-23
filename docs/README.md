# 📚 RealFreelancer Documentation

> ⚠️ **Important: Local Development Only!**
>
> This platform is for local/portfolio use. **Do NOT deploy publicly without major security upgrades.**

---

## 🚀 Overview

RealFreelancer is a free, open-source freelance platform for developers to build, learn, and showcase their skills. Use it for your portfolio, hackathons, or as a learning playground!

- [Main README](../README.md)
- [Quick Start](../QUICK_START.md)
- [Portfolio Showcase](../PORTFOLIO_SHOWCASE.md)

---

## ✨ Features

- **Project Management**: Post, browse, and apply for projects
- **Real-time Chat**: Communicate between clients and freelancers (beta)
- **Reputation System**: Earn badges and reviews (coming soon)
- **File Sharing**: Upload project files (5MB max)
- **Skill-based Matching**: Filter projects by required skills
- **Portfolio Integration**: Share your profile link on GitHub

---

## 🛠️ Tech Stack

- **Backend**: Spring Boot 3.x (Java 17+)
- **Frontend**: Next.js 14.x (React 18, TypeScript)
- **Database**: H2 (dev), PostgreSQL (prod)
- **Styling**: Tailwind CSS
- **Authentication**: JWT tokens
- **File Storage**: Base64 in DB (local) or Cloudinary (optional)

---

## ⚡ Quick Setup

```bash
git clone https://github.com/MedAmestak/realfreelancer.git
cd realfreelancer
# See QUICK_START.md for full instructions
```

---

## 🔑 Environment Variables

### Backend (.env)
```env
JWT_SECRET=YOUR_JWT_SECRET
DB_USERNAME=sa
DB_PASSWORD=
SPRING_PROFILES_ACTIVE=dev
```
### Frontend (.env)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

---

## 🗄️ Database Setup

- **Development**: H2 (in-memory, no setup)
- **Production**: PostgreSQL (see .env and docker-compose)

---

## 📡 API Endpoints (Key Examples)

### Authentication
- `POST /api/auth/register` — Register new user
- `POST /api/auth/login` — User login
- `GET /api/auth/profile` — Get current user profile
- `PUT /api/auth/profile` — Update current user profile

### Projects
- `GET /api/projects` — List all projects (with filtering)
- `POST /api/projects` — Create new project
- `GET /api/projects/{id}` — Get project details
- `POST /api/projects/{id}/apply` — Apply for project
- `GET /api/projects/featured` — Get featured projects

### Chat
- `GET /api/chat/{conversationId}` — Get chat messages
- `POST /api/chat/{conversationId}` — Send message

### Reviews & Badges
- `POST /api/reviews` — Submit review
- `GET /api/users/{id}/badges` — Get user badges

---

## 🏗️ Project Structure

```
realfreelancer/
├── backend/                 # Spring Boot application
│   ├── src/main/java/com/realfreelancer/
│   │   ├── controller/  # REST endpoints
│   │   ├── model/       # JPA entities
│   │   ├── repository/  # Data access layer
│   │   ├── service/     # Business logic
│   │   └── config/      # Security & configuration
│   └── src/main/resources/
│       └── application.yml
├── frontend/                # Next.js application
│   ├── app/                # Next.js pages
│   ├── components/         # React components
│   ├── styles/             # Tailwind CSS
│   └── public/             # Static assets
└── docs/                   # Documentation
```

---

## 🛡️ Security Features

- JWT-based authentication
- Input validation & sanitization
- File size limits (5MB max)
- CORS for localhost only
- SQL injection & XSS protection

---

## 🧑‍💻 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test locally
5. Submit a pull request

---

## 🐞 FAQ & Troubleshooting

**Q: Why can't I log in after registering?**
- A: Make sure the backend is running and your JWT secret is set correctly in `.env`.

**Q: How do I reset the database?**
- A: Stop all containers, delete the Docker volume `postgres-data`, and restart.

**Q: Can I deploy this to production?**
- A: Not without major security upgrades! This is for local/portfolio use only.

**Q: Where can I get help?**
- A: Open an issue on GitHub or check the main README for links.

---

## 📄 License

MIT License — see LICENSE file for details.

---

**Remember:** This platform is for local development and portfolio building. **Do not deploy to production without additional security measures.** 