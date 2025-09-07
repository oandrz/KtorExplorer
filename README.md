# Ktor Test Project

This is a comprehensive Ktor 3.x (Netty) server showcasing modern web development practices including REST APIs, AI integration, and external service consumption.

## 📚 Documentation Index

- **[PROJECT_INDEX.md](.agents/knowledge/PROJECT_INDEX.md)** - Complete project documentation and architecture overview
- **[API_REFERENCE.md](.agents/knowledge/API_REFERENCE.md)** - Detailed API endpoint documentation with examples  
- **[DEVELOPMENT_GUIDE.md](.agents/knowledge/DEVELOPMENT_GUIDE.md)** - Development setup, patterns, and best practices

## 🎯 Features

- ✅ **RESTful APIs**: TODO management and blog endpoints
- ✅ **AI Integration**: OpenAI-powered assistant with custom tools
- ✅ **External APIs**: Pokemon API integration with detailed models
- ✅ **Dynamic Port Allocation**: Automatic port discovery (8080-9000)
- ✅ **Modern Kotlin**: Coroutines, serialization, and type-safe development
- ✅ **Comprehensive Logging**: Structured logging with Logback
- ✅ **Thread-Safe Operations**: Mutex-protected concurrent operations

Prerequisites
- JDK 21 (the build is configured to use JVM toolchain 21)
- Internet access for dependencies (Gradle will download them via the wrapper)
- Optional: IntelliJ IDEA (Ktor plugin not required)

How to run
Option A — Gradle (recommended)
1) From the project root, build and run:
   ./gradlew run

2) Watch the console output. The app selects a free port between 8080 and 9000 at startup and prints it, for example:
   Starting server on port 8081
   Open your browser at: http://localhost:8081

3) Open the printed URL in your browser or use curl examples below (replace 8081 with the printed port).

Option B — IntelliJ IDEA
1) Open the project in IntelliJ.
2) Find the main function in src/main/kotlin/org/example/Main.kt and run it.
   - It will print the chosen port (same behavior as Gradle run).

Notes on configuration
- The application.conf sets deployment.port = 8080, but the runtime port is actually chosen dynamically in Main.kt. The printed port in the console is the source of truth.
- JSON support is enabled via ContentNegotiation + kotlinx.serialization.
- Logging is via Logback; see src/main/resources/logback.xml for levels.

Key endpoints (examples)
- Health/root:
  GET /
  curl http://localhost:8081/
  -> "Hello, Ktor! The server is running correctly!"

- TODOs:
  GET /todos
  curl http://localhost:8081/todos

  POST /todos (create a todo)
  curl -X POST http://localhost:8081/todos \
       -H "Content-Type: application/json" \
       -d '{"title":"Buy milk"}'

  GET /todos/{id}
  curl http://localhost:8081/todos/<id>

  PUT /todos/{id}/toggle
  curl -X PUT http://localhost:8081/todos/<id>/toggle

  DELETE /todos/{id}
  curl -X DELETE http://localhost:8081/todos/<id>

- Blog (sample routes):
  See src/main/kotlin/org/example/blog for available paths. You can explore /blog-related endpoints if exposed by BlogRouting.kt.

- AI demo endpoint (optional):
  POST /ai/query
  Content-Type: application/x-www-form-urlencoded
  Parameter: userPrompt=...
  Note: The current implementation in Routing.kt uses a hardcoded API key stub; you likely won’t want to use this in production. This endpoint is for demonstration and may not work without valid credentials and dependencies.

Troubleshooting
- Port in use: The app auto-picks the next free port; just check the console for the actual port.
- Build errors: Ensure JDK 21 is configured. You can verify with:  ./gradlew --version
- Clean and rebuild if needed:  ./gradlew clean build

Build and test
- Build:  ./gradlew build
- Run tests (if any):  ./gradlew test

Project entrypoints
- Main class: org.example.MainKt (configured in build.gradle.kts)
- Ktor module: org.example.ApplicationKt.module

Enjoy hacking with Ktor!