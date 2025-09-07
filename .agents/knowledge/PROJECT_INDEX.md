# Ktor Test Project - Complete Documentation Index

## 📋 Project Overview

**Project**: Ktor Test Application  
**Version**: 1.0-SNAPSHOT  
**Framework**: Ktor 3.2.0  
**Language**: Kotlin 2.1.20  
**JVM Target**: 21  
**Group**: org.example  

A demonstration Ktor server showcasing:
- RESTful API endpoints
- JSON serialization with Kotlin serialization
- AI integration with tool support
- Pokemon API integration
- TODO management system
- Blog functionality
- Dynamic port allocation

## 🏗️ Architecture Overview

### Core Framework Stack
- **Server Engine**: Netty
- **Serialization**: kotlinx.serialization JSON
- **Content Negotiation**: Ktor Content Negotiation
- **AI Integration**: Koog Agents (v0.2.1)
- **Logging**: Logback Classic
- **Coroutines**: Kotlinx Coroutines

### Project Structure
```
src/main/kotlin/org/example/
├── Main.kt                     # Application entry point with dynamic port allocation
├── Routing.kt                  # Main routing configuration and AI endpoints
├── config/
│   └── Config.kt              # Configuration management (local.properties)
├── blog/                      # Blog feature module
│   ├── BlogRouting.kt         # Blog API routing
│   ├── model/                 # Blog data models
│   └── service/               # Blog business logic
├── pokemon/                   # Pokemon API integration
│   ├── model/                 # Pokemon data models
│   └── service/               # Pokemon API client
├── todo/                      # TODO management system
│   └── TodoService.kt         # TODO CRUD operations
├── tools/                     # AI agent tools
│   ├── PokemonInfoTool.kt     # Pokemon information tool for AI
│   ├── PokemonInfoToolSet.kt  # Pokemon tool registry
│   └── SomeToolSet.kt         # Additional tools
└── util/                      # Utility classes
    └── ApiParams.kt           # API parameter constants
```

## 📚 Component Documentation

### 🚀 Application Entry Point

**File**: `src/main/kotlin/org/example/Main.kt`

#### Dynamic Port Allocation
- **Function**: `findAvailablePort(startPort: Int = 8080, endPort: Int = 9000): Int`
- **Purpose**: Automatically finds available port between 8080-9000
- **Benefits**: Prevents port conflicts in development environments
- **Error Handling**: Throws `IllegalStateException` if no ports available

#### Application Bootstrap
- **Server Engine**: Netty embedded server
- **Module Configuration**: Delegates to `Application::module`
- **Console Output**: Displays selected port and browser URL

### 🛣️ Routing System

**File**: `src/main/kotlin/org/example/Routing.kt`

#### Route Organization
1. **Default Routes** (`configureDefaultRouting()`)
   - `GET /` - Health check endpoint
   - Returns: "Hello, Ktor! The server is running correctly!"

2. **AI Routes** (`configureAIRouting()`)
   - `GET /ai` - AI endpoint information
   - `POST /ai/query` - AI query processing with tool support

3. **TODO Routes** (`configureTodoRouting()`)
   - Full CRUD operations for TODO management
   - Comprehensive error handling and logging

4. **Blog Routes** (`configureBlogRouting()`)
   - Blog post creation and retrieval
   - Form-based content submission

#### AI Integration Details
- **Model**: OpenAI GPT-4.1
- **System Prompt**: JSON-only response format enforcement
- **Tools Available**:
  - `SayToUser`: Direct user communication
  - `PokemonInfoTool`: Pokemon data retrieval
- **Security**: Hardcoded API key (demo only)

### 📝 TODO Management System

**File**: `src/main/kotlin/org/example/todo/TodoService.kt`

#### Data Model
```kotlin
@Serializable
data class Todo(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val completed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
```

#### Service Features
- **Thread Safety**: Mutex-protected operations
- **Async Operations**: Coroutine-based with simulated delays
- **CRUD Operations**:
  - `getAllTodos()`: Retrieve all todos (500ms delay)
  - `getTodoById(id)`: Get specific todo (300ms delay)
  - `addTodo(title)`: Create new todo (400ms delay)
  - `toggleTodo(id)`: Toggle completion status (300ms delay)
  - `deleteTodo(id)`: Remove todo (300ms delay)

#### API Endpoints
- `GET /todos` - List all todos
- `GET /todos/{id}` - Get specific todo
- `POST /todos` - Create new todo
- `PUT /todos/{id}/toggle` - Toggle completion
- `DELETE /todos/{id}` - Delete todo

### 🐾 Pokemon API Integration

**File**: `src/main/kotlin/org/example/pokemon/service/PokemonService.kt`

#### Service Configuration
- **Base URL**: `https://pokeapi.co/api/v2`
- **HTTP Client**: Ktor client with JSON content negotiation
- **JSON Settings**: Pretty print, lenient parsing, ignore unknown keys

#### Available Methods
1. **`getPokemonList(limit, offset)`**
   - Paginated Pokemon retrieval
   - Default: 1000 limit, 0 offset
   - Returns: `Result<PokemonListResponse>`

2. **`getPokemonDetails(idOrName)`**
   - Detailed Pokemon information
   - Accepts ID or name
   - Returns: `Result<PokemonDetails>`

3. **`getNextPage(currentResponse)`**
   - Navigate to next results page
   - Returns: `Result<PokemonListResponse>?`

4. **`getPreviousPage(currentResponse)`**
   - Navigate to previous results page
   - Returns: `Result<PokemonListResponse>?`

5. **`getPokemonNamesStartingWith(initCharacter)`**
   - Filter Pokemon by starting character
   - Case-insensitive matching
   - Returns: `List<PokemonEntry>`

#### Data Models (`pokemon/model/PokemonModels.kt`)
- **PokemonListResponse**: Paginated list container
- **PokemonEntry**: Basic Pokemon information
- **PokemonDetails**: Complete Pokemon data
- **PokemonSprites**: Image URLs
- **PokemonTypeSlot**: Type information
- **NamedApiResource**: Referenced resources

### 📖 Blog System

**Files**: `src/main/kotlin/org/example/blog/`

#### Blog Data Models
1. **BlogPost**: Core blog post entity
2. **BlogPostForm**: Form data transfer object

#### Blog API Endpoints
- `GET /blog/list` - Retrieve all blog posts
- `POST /blog/post` - Create new blog post
  - Parameters: `title`, `content`
  - Default author: "oink"

#### Service Layer
**BlogApiService**: Handles blog post persistence and retrieval

### 🤖 AI Tools Integration

**Files**: `src/main/kotlin/org/example/tools/`

#### PokemonInfoTool
- **Purpose**: Provides Pokemon information to AI agent
- **Integration**: Part of AI tool registry
- **Functionality**: Retrieves Pokemon data via PokemonService

#### Tool Architecture
- **Base**: Koog Agents framework
- **Registry Pattern**: Modular tool registration
- **AI Integration**: Seamless LLM tool calling

### ⚙️ Configuration Management

**File**: `src/main/kotlin/org/example/config/Config.kt`

#### Local Properties Support
- **File**: `local.properties`
- **Purpose**: Environment-specific configuration
- **Usage**: API keys, feature flags, environment settings

## 🔧 Build & Development

### Dependencies Overview
```kotlin
// Core Ktor
implementation("io.ktor:ktor-server-core-jvm:3.2.0")
implementation("io.ktor:ktor-server-netty-jvm:3.2.0")
implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.0")
implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.0")

// Kotlin Ecosystem
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

// AI Integration
implementation("ai.koog:koog-agents:0.2.1")

// Logging
implementation("ch.qos.logback:logback-classic:1.4.14")
```

### Build Commands
```bash
# Build project
./gradlew build

# Run application
./gradlew run

# Run tests
./gradlew test

# Clean build
./gradlew clean build
```

### Development Setup
1. **JDK Requirement**: Java 21
2. **IDE**: IntelliJ IDEA (recommended)
3. **Build Tool**: Gradle with Kotlin DSL
4. **Main Class**: `org.example.MainKt`

## 🌐 API Reference

### Health Check
```http
GET /
Accept: text/plain

Response: "Hello, Ktor! The server is running correctly!"
```

### TODO Operations
```http
# List all todos
GET /todos
Accept: application/json

# Create todo
POST /todos
Content-Type: application/json
Body: {"title": "Buy milk"}

# Get specific todo
GET /todos/{id}
Accept: application/json

# Toggle completion
PUT /todos/{id}/toggle
Accept: application/json

# Delete todo
DELETE /todos/{id}
```

### AI Integration
```http
# AI Query
POST /ai/query
Content-Type: application/x-www-form-urlencoded
Body: userPrompt=Tell me about Pikachu

# AI Endpoint Info
GET /ai
Accept: application/json
```

### Blog Operations
```http
# List blog posts
GET /blog/list
Accept: application/json

# Create blog post
POST /blog/post
Content-Type: application/x-www-form-urlencoded
Body: title=My Post&content=Post content
```

## 📊 Performance Characteristics

### Simulated Delays
- **TODO Operations**: 300-500ms (simulates database operations)
- **Pokemon API**: Real network latency to PokeAPI
- **AI Processing**: Depends on OpenAI response times

### Concurrency
- **Thread Safety**: Mutex-protected TODO operations
- **Async Processing**: Full coroutine support
- **Connection Pooling**: Ktor HTTP client with connection reuse

## 🔐 Security Considerations

### Current Security Status
- **AI API Key**: Hardcoded (development only)
- **Input Validation**: Basic parameter validation
- **Error Handling**: Comprehensive with logging
- **CORS**: Not configured
- **Authentication**: Not implemented

### Production Recommendations
1. **Environment Variables**: Move API keys to environment variables
2. **Input Validation**: Add comprehensive request validation
3. **Rate Limiting**: Implement API rate limiting
4. **Authentication**: Add user authentication system
5. **HTTPS**: Enable TLS in production
6. **CORS**: Configure CORS policies

## 🚀 Deployment

### Local Development
1. Clone repository
2. Ensure JDK 21 is installed
3. Run `./gradlew run`
4. Access server at printed port

### Configuration Files
- **Application Config**: `src/main/resources/application.conf`
- **Logging Config**: `src/main/resources/logback.xml`
- **Local Properties**: `local.properties`

### Port Management
- **Dynamic Allocation**: Automatically finds available port
- **Range**: 8080-9000
- **Console Output**: Shows selected port for access

## 🔍 Troubleshooting

### Common Issues
1. **Port Conflicts**: App auto-selects free port
2. **Build Errors**: Verify JDK 21 configuration
3. **Clean Rebuild**: Use `./gradlew clean build`
4. **API Connectivity**: Check internet connection for Pokemon API

### Logging
- **Level**: Configurable via logback.xml
- **Output**: Console and file (configurable)
- **Components**: Separate loggers for routing, blog, services

## 📈 Extension Points

### Adding New Features
1. **New API Endpoints**: Extend routing configuration
2. **Additional AI Tools**: Implement tool interface
3. **Data Models**: Add serializable data classes
4. **Services**: Create new service classes with dependency injection

### Integration Opportunities
- **Database**: Replace in-memory storage with persistent database
- **Authentication**: Add user management and JWT tokens
- **WebSocket**: Enable real-time communication
- **File Upload**: Add file handling capabilities
- **Testing**: Implement comprehensive test suite

## 📚 Code Quality

### Architecture Patterns
- **Separation of Concerns**: Clear module boundaries
- **Dependency Injection**: Service injection in routing
- **Error Handling**: Comprehensive exception handling
- **Async Programming**: Coroutine-based operations
- **Resource Management**: Proper HTTP client lifecycle

### Code Standards
- **Kotlin Conventions**: Standard Kotlin coding style
- **Documentation**: Comprehensive KDoc comments
- **Error Messages**: Meaningful error responses
- **Logging**: Structured logging with context

---

*Last Updated: Generated by Claude Code*