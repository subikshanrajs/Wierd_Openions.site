# Weird Opinions Platform

A fun, anonymous platform where people can ask weird questions and get honest opinions from others. Built with Spring Boot (Java) backend and React frontend.

## 🌟 Features

### Core Features
- **Anonymous Question Posting**: Users can ask weird questions with generated anonymous names
- **Categories**: Organize questions by categories (Social Norms, Personal Habits, Weird Food Combos, etc.)
- **Polling System**: Add polls to questions for quick opinions
- **Voting System**: Upvote/downvote questions and comments
- **Comments & Replies**: Threaded comment system for discussions
- **User Authentication**: Secure JWT-based authentication
- **Trending Questions**: See the most popular questions this week

### Special Features
- **Anonymous Names**: Users get fun anonymous names like "Curious Otter 123"
- **Image Support**: Attach images to questions
- **Search**: Find questions by keywords
- **Responsive Design**: Works great on mobile and desktop
- **Real-time Updates**: Vote counts and comments update dynamically

## 🏗️ Project Structure

```
weird-opinions-app/
├── backend/                    # Spring Boot Backend
│   ├── src/main/java/com/yourcompany/weirdopinions/
│   │   ├── controller/         # REST API Controllers
│   │   ├── model/             # JPA Entities
│   │   ├── repository/        # Data Access Layer
│   │   ├── service/           # Business Logic
│   │   ├── security/          # JWT & Security Config
│   │   └── WeirdOpinionsApplication.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml               # Maven Dependencies
│   └── Dockerfile
├── frontend/                 # React Frontend
│   ├── public/
│   ├── src/
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml        # Docker Setup
└── README.md
```

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18+ and npm
- Maven 3.6+
- Docker & Docker Compose (optional)

### Option 1: Run with Docker (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd weird-opinions-app
   ```

2. **Start with Docker Compose**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080
   - H2 Database Console: http://localhost:8080/h2-console

### Option 2: Run Locally

#### Backend Setup
1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Install dependencies and run**
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

   The backend will start on http://localhost:8080

#### Frontend Setup
1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm start
   ```

   The frontend will start on http://localhost:3000

## 🗄️ Database Configuration

### Development (H2 In-Memory)
The application uses H2 in-memory database by default for easy development:
- **URL**: `jdbc:h2:mem:weirdopinionsdb`
- **Username**: `sa`
- **Password**: `password`
- **Console**: http://localhost:8080/h2-console

### Production (PostgreSQL)
For production, update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/weirdopinions
spring.datasource.username=weirduser
spring.datasource.password=weirdpass
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## 🔧 Configuration

### Backend Configuration (`application.properties`)
```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:h2:mem:weirdopinionsdb
spring.datasource.username=sa
spring.datasource.password=password

# JWT Configuration
app.jwtSecret=weirdOpinionsSecretKey
app.jwtExpirationInMs=86400000

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### Frontend Configuration
Create a `.env` file in the frontend directory:
```env
REACT_APP_API_BASE_URL=http://localhost:8080/api
```

## 📱 API Endpoints

### Authentication
- `POST /api/auth/signin` - User login
- `POST /api/auth/signup` - User registration
- `GET /api/auth/check-username` - Check username availability
- `GET /api/auth/check-email` - Check email availability

### Questions
- `GET /api/questions` - Get all questions (paginated)
- `GET /api/questions/{id}` - Get question by ID
- `POST /api/questions` - Create new question (auth required)
- `PUT /api/questions/{id}` - Update question (auth required)
- `DELETE /api/questions/{id}` - Delete question (auth required)
- `GET /api/questions/category/{category}` - Get questions by category
- `GET /api/questions/trending` - Get trending questions
- `GET /api/questions/search` - Search questions
- `POST /api/questions/{id}/vote` - Vote on question (auth required)
- `POST /api/questions/{id}/poll-vote` - Vote on poll (auth required)

### Comments
- `GET /api/questions/{id}/comments` - Get question comments
- `POST /api/comments` - Create comment (auth required)
- `POST /api/comments/reply` - Create reply (auth required)
- `PUT /api/comments/{id}` - Update comment (auth required)
- `DELETE /api/comments/{id}` - Delete comment (auth required)
- `POST /api/comments/{id}/vote` - Vote on comment (auth required)

### Users
- `GET /api/users/me` - Get current user info (auth required)
- `GET /api/users/my-questions` - Get user's questions (auth required)
- `GET /api/users/my-comments` - Get user's comments (auth required)
- `POST /api/users/regenerate-anonymous-name` - Generate new anonymous name (auth required)

## 🎨 Categories

The platform supports these question categories:
- **Social Norms** - Questions about social expectations
- **Personal Habits** - Quirky personal behaviors
- **Weird Food Combos** - Strange food combinations
- **Home Life** - Domestic oddities
- **Cringe Confessions** - Embarrassing admissions
- **Bathroom Etiquette** - Bathroom-related questions
- **Pet Peculiarities** - Weird pet behaviors
- **Public Transport Mysteries** - Commuting oddities
- **Fashion Choices** - Style and clothing questions
- **Other** - Miscellaneous weird questions

## 🔒 Security Features

- **JWT Authentication** - Secure token-based authentication
- **Password Encryption** - BCrypt password hashing
- **Anonymous Names** - Users identified by generated names
- **Data Privacy** - No personal data exposed in public posts
- **CORS Configuration** - Proper cross-origin request handling
- **Input Validation** - Server-side validation of all inputs

## 🚀 Deployment

### Docker Deployment
```bash
# Build and run with Docker Compose
docker-compose up --build -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Manual Deployment

#### Backend
```bash
cd backend
./mvnw clean package
java -jar target/weird-opinions-0.0.1-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend
npm run build
# Serve the build directory with a web server
```

## 🧪 Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

### Frontend Tests
```bash
cd frontend
npm test
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🎯 Future Features

- [ ] Real-time notifications
- [ ] Admin dashboard for moderation
- [ ] Mobile app (React Native)
- [ ] Social sharing features
- [ ] Question of the day
- [ ] User badges and achievements
- [ ] Advanced search filters
- [ ] Dark mode theme
- [ ] Multi-language support
- [ ] Question analytics

## 🐛 Troubleshooting

### Common Issues

**Backend not starting:**
- Check if Java 17+ is installed
- Ensure port 8080 is available
- Verify database connection

**Frontend not loading:**
- Check if Node.js 18+ is installed
- Ensure port 3000 is available
- Verify API connection to backend

**CORS errors:**
- Update CORS configuration in SecurityConfig
- Check frontend API base URL configuration

**Database issues:**
- Access H2 console to verify data
- Check application.properties configuration
- Ensure database schema is created

## 📞 Support

If you encounter any issues or have questions:
1. Check the troubleshooting section
2. Create an issue on GitHub
3. Contact the development team

---

**Happy Weird Questioning! 🤔✨**