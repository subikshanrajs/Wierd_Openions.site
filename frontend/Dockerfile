version: '3.8'

services:
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - SPRING_DATASOURCE_URL=jdbc:h2:mem:weirdopinionsdb
      - SPRING_DATASOURCE_USERNAME=sa
      - SPRING_DATASOURCE_PASSWORD=password
      - APP_JWT_SECRET=weirdOpinionsSecretKeyForDocker
      - APP_JWT_EXPIRATION=86400000
    volumes:
      - ./backend:/app
    depends_on:
      - postgres
    networks:
      - weird-opinions-network

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=weirdopinions
      - POSTGRES_USER=weirduser
      - POSTGRES_PASSWORD=weirdpass
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    networks:
      - weird-opinions-network

  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    ports:
      - "3000:3000"
    environment:
      - REACT_APP_API_BASE_URL=http://localhost:8080/api
    depends_on:
      - backend
    volumes:
      - ./frontend:/app
      - /app/node_modules
    networks:
      - weird-opinions-network

volumes:
  postgres_data:

networks:
  weird-opinions-network:
    driver: bridge