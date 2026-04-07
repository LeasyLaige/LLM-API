# LLM-API

A full-stack LLM chat application with an Angular frontend, Spring Boot backend, Django LLM service, and MySQL database—all orchestrated with Docker Compose.

## Architecture

```
┌──────────────────────────────────────────────────┐
│          Angular Frontend (LLM-Frontend)         │
│  Chat-style UI (ChatGPT/Claude/Grok inspired)   │
│  Port: 80 (Nginx)                                │
└────────────────────┬─────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────┐
│      Spring Boot API (LLM-Springboot)            │
│  REST API, JPA/MySQL persistence                │
│  Port: 8080                                      │
└────────────────────┬─────────────────────────────┘
                     │
                     ▼
┌──────────────────────────────────────────────────┐
│   Django LLM Service (LLM-Python-Django)         │
│  Hugging Face API calls (Meta-Llama-3-8B)       │
│  Gunicorn WSGI server                            │
│  Port: 8000                                      │
└────────────────────┬─────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │   MySQL Database      │
         │   Port: 3306          │
         └───────────────────────┘
```

### Technology Stack

- **Frontend:** Angular 20, Zoneless Change Detection, SCSS, Signals
- **Backend API:** Spring Boot 3.x, Java 17, JPA/Hibernate, MySQL
- **LLM Service:** Django 4.x, Hugging Face Transformers, Gunicorn
- **DevOps:** Docker, Docker Compose, Nginx
- **API Protocol:** REST (HTTP/JSON)

## Prerequisites

- Docker and Docker Compose
- Git
- (Optional) Node.js 22+ for local frontend development
- (Optional) Maven 3.8+ for local Spring Boot builds
- (Optional) Python 3.10+ for local Django development

## Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/LeasyLaige/LLM-API.git
cd LLM-API
```

### 2. Start All Services
```bash
docker-compose up -d
```

This will:
- Build and start MySQL
- Build and start Django LLM service
- Build and start Spring Boot API
- Build and start Angular frontend (Nginx)

All services are on the `ai-network` bridge network for inter-service communication.

### 3. Access the Application
- **Frontend:** http://localhost
- **Spring Boot API:** http://localhost:8080
- **Django LLM Service:** http://localhost:8000

### 4. Stop All Services
```bash
docker-compose down
```

## Project Structure

```
LLM-API/
├── LLM-Frontend/                 # Angular 20 chat UI
│   ├── src/
│   │   ├── app/
│   │   │   ├── app.ts           # Main component (signals, chat logic)
│   │   │   ├── app.html         # Chat template
│   │   │   ├── app.scss         # Chat styles
│   │   │   ├── models/          # TypeScript interfaces
│   │   │   └── services/        # LlmService (HTTP)
│   │   ├── styles.scss          # Global styles
│   │   └── index.html
│   ├── Dockerfile               # Multi-stage Node → Nginx build
│   ├── nginx.conf               # SPA routing config
│   └── package.json
│
├── LLM-Springboot/              # Spring Boot API & services
│   ├── LLM-Data/               # JPA entities, repositories
│   ├── LLM-SB/                 # Main Spring Boot app
│   │   ├── src/
│   │   │   ├── controller/      # REST endpoints
│   │   │   ├── service/         # Business logic
│   │   │   ├── entity/         # JPA entities
│   │   │   └── dto/            # Request/Response DTOs
│   │   ├── pom.xml
│   │   └── Dockerfile
│   └── docker-compose.yml (local)
│
├── LLM-Python-Django/           # Django LLM service
│   ├── llm_django/
│   │   ├── api/                # Django apps
│   │   └── settings.py
│   ├── Dockerfile              # Gunicorn WSGI server
│   ├── requirements.txt
│   └── .env
│
└── docker-compose.yml          # Orchestrates all 4 services
```

## Key Features

### Angular Frontend
- **Chat-style UI** — Scrollable conversation history showing user prompts and assistant responses
- **Ephemeral chat** — Messages clear on page refresh (no persistence)
- **Responsive design** — Mobile-friendly with pill-shaped input and send button
- **Zoneless change detection** — Modern Angular performance optimization
- **Auto-scroll** — Jumps to latest message on new response

### Spring Boot API
- **REST endpoint** `/api/llm/ask` — Accepts `{ "prompt": "..." }`, returns `{ "prompt", "response", "createdAt" }`
- **MySQL persistence** — Saves all Q&A sessions for auditing
- **Calls Django backend** — Delegates LLM inference to Hugging Face
- **Error handling** — Returns 500 -> 200 status based on backend availability

### Django LLM Service
- **Hugging Face integration** — Uses Meta-Llama-3-8B-Instruct model
- **Gunicorn WSGI server** — Handles concurrent requests, fixes chunked encoding issues
- **CORS enabled** — Accepts requests from Spring Boot and frontend
- **Timeout 120s** — Allows time for model inference

### MySQL Database
- **Persists** — Prompt request, LLM response, timestamp
- **Health checks** — Ensures Spring Boot waits before connecting

## API Contracts

### POST `/api/llm/ask`
**Request:**
```json
{
  "prompt": "What is machine learning?"
}
```

**Response (200 OK):**
```json
{
  "prompt": "What is machine learning?",
  "response": "Machine learning is... [LLM generates response]",
  "createdAt": "2026-03-07T12:51:33Z"
}
```

## Development

### Local Frontend Build
```bash
cd LLM-Frontend
npm install
ng build
ng serve
# Opens on http://localhost:4200
```

### Local Spring Boot Build
```bash
cd LLM-Springboot
mvn clean package -DskipTests
java -jar LLM-SB/target/LLM-SB-0.0.1-SNAPSHOT.jar
# Runs on http://localhost:8080
```

### Local Django Setup
```bash
cd LLM-Python-Django
python -m venv venv
source venv/bin/activate  # or venv\Scripts\activate on Windows
pip install -r requirements.txt
export HUGGINGFACE_API_KEY="your-key-here"
gunicorn llm_django.wsgi:application --bind 0.0.0.0:8000 --workers 2 --timeout 120
# Runs on http://localhost:8000
```

## Configuration

### Environment Variables

**LLM-Python-Django** (`.env` file):
```
HUGGINGFACE_API_KEY=your-hf-token-here
CORS_ALLOWED_ORIGIN=http://localhost:8080,http://llm-springboot:8080
```

**LLM-Springboot** (docker-compose.yml):
```yaml
SPRING_DATASOURCE_URL: jdbc:mysql://llm-mysql:3306/llmsb?...
SPRING_DATASOURCE_USERNAME: root
SPRING_DATASOURCE_PASSWORD: MadebyRootMysql399!
```

## Troubleshooting

### Frontend shows "Cannot connect to backend"
- Verify Spring Boot container is running: `docker-compose ps`
- Check logs: `docker-compose logs llm-springboot`
- Ensure firewall allows port 8080

### Django returns 500 error
- Check Hugging Face API key in `.env`
- Verify model is available: https://huggingface.co/meta-llama/Llama-2-7b-chat
- Check Django logs: `docker-compose logs llm-django`

### MySQL connection refused
- Ensure MySQL is healthy: `docker-compose ps` (should show "healthy")
- Check credentials match in Spring Boot env vars
- Give MySQL 10-15 seconds to initialize on first run

## Known Issues

- Chat history is in-memory only (no persistence on page refresh)
- Large responses (>1000 tokens) may take 30+ seconds
- Model inference latency depends on server hardware

## Future Enhancements

- [ ] Persistent chat history with user accounts
- [ ] Multiple LLM model selection
- [ ] Chat export (PDF/Markdown)
- [ ] Real-time streaming responses (Server-Sent Events)
- [ ] Rate limiting and API key authentication
- [ ] Support for image uploads and vision models

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License — see the LICENSE file for details.

## Contact

**Author:** Lander Suarez  
**GitHub:** [LeasyLaige](https://github.com/LeasyLaige)  
**Repository:** [LLM-API](https://github.com/LeasyLaige/LLM-API)

---

**Built with ❤️ using Angular, Spring Boot, Django, and Docker**
