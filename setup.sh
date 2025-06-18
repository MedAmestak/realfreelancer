#!/bin/bash

echo "🚀 RealFreelancer Setup Script"
echo "================================"

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17 or higher."
    exit 1
fi

# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js 18 or higher."
    exit 1
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven."
    exit 1
fi

echo "✅ Prerequisites check passed!"

# Setup Backend
echo ""
echo "📦 Setting up Backend..."
cd backend

# Copy environment file
if [ ! -f .env ]; then
    cp env.example .env
    echo "✅ Created .env file from template"
else
    echo "ℹ️  .env file already exists"
fi

# Install dependencies and start backend
echo "🔧 Installing backend dependencies..."
mvn clean install -DskipTests

echo ""
echo "🚀 Starting backend server..."
echo "Backend will be available at http://localhost:8080"
echo "H2 Console: http://localhost:8080/h2-console"
echo ""
echo "Press Ctrl+C to stop the backend server"
echo ""

# Start backend in background
mvn spring-boot:run &
BACKEND_PID=$!

# Wait a bit for backend to start
sleep 10

# Setup Frontend
echo ""
echo "📦 Setting up Frontend..."
cd ../frontend

# Copy environment file
if [ ! -f .env ]; then
    cp env.example .env
    echo "✅ Created .env file from template"
else
    echo "ℹ️  .env file already exists"
fi

# Install dependencies
echo "🔧 Installing frontend dependencies..."
npm install

echo ""
echo "🚀 Starting frontend server..."
echo "Frontend will be available at http://localhost:3000"
echo ""
echo "Press Ctrl+C to stop both servers"
echo ""

# Start frontend
npm run dev &
FRONTEND_PID=$!

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "🛑 Stopping servers..."
    kill $BACKEND_PID 2>/dev/null
    kill $FRONTEND_PID 2>/dev/null
    echo "✅ Servers stopped"
    exit 0
}

# Set up signal handlers
trap cleanup SIGINT SIGTERM

# Wait for both processes
wait 