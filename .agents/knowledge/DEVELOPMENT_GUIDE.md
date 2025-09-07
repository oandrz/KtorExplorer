# Development Guide - Ktor Test Project

## 🚀 Quick Start

### Prerequisites
- **JDK 21** - Required for Kotlin 2.1.20
- **Git** - For version control
- **IntelliJ IDEA** - Recommended IDE (optional)

### Initial Setup
```bash
# Clone repository
git clone <repository-url>
cd ktor-test

# Verify Java version
java -version
# Should show Java 21

# Build project
./gradlew build

# Run application
./gradlew run
```

### First Run
1. Start the application with `./gradlew run`
2. Note the console output for the dynamic port:
   ```
   Starting server on port 8081
   Open your browser at: http://localhost:8081
   ```
3. Test the health endpoint: `curl http://localhost:8081/`

---

## 🏗️ Project Architecture

### Module Organization
```
src/main/kotlin/org/example/
├── Main.kt                    # Application entry point
├── Routing.kt                 # Main routing configuration
├── config/                    # Configuration management
├── blog/                      # Blog feature module
├── pokemon/                   # Pokemon API integration
├── todo/                      # TODO management
├── tools/                     # AI agent tools
└── util/                      # Utilities
```

### Key Architectural Patterns

#### 1. Module-Based Organization
Each feature is organized into its own package with clear separation of concerns:
- **Models**: Data classes with serialization
- **Services**: Business logic and external API integration
- **Routing**: HTTP endpoint definitions

#### 2. Dependency Injection
Services are injected into routing functions:
```kotlin
internal fun Application.configureRouting(
    pokemonService: PokemonService,
    todoService: TodoService,
    blogApiService: BlogApiService
) {
    // Route configuration
}
```

#### 3. Async-First Design
All operations use coroutines for non-blocking I/O:
```kotlin
suspend fun getAllTodos(): List<Todo> = withContext(Dispatchers.Default) {
    delay(500) // Simulate async operation
    mutex.withLock { todos.toList() }
}
```

#### 4. Result-Based Error Handling
External API calls return `Result` types:
```kotlin
suspend fun getPokemonList(): Result<PokemonListResponse> = runCatching {
    client.get("$baseUrl/pokemon").body()
}
```

---

## 🛠️ Development Environment

### IDE Setup (IntelliJ IDEA)

#### Kotlin Configuration
1. **File** → **Settings** → **Languages & Frameworks** → **Kotlin**
2. Set **Kotlin Compiler** version to 2.1.20
3. Enable **Coroutines** support

#### Code Style
```kotlin
// Use 4-space indentation
// Prefer expression bodies for single expressions
fun getMessage() = "Hello, World!"

// Use trailing commas in multi-line structures
data class User(
    val id: String,
    val name: String,
    val email: String,
)
```

#### Useful Plugins
- **Ktor** - For Ktor-specific features
- **Serialization** - For Kotlin serialization support
- **HTTP Client** - For testing API endpoints

### Build System (Gradle)

#### Key Build Files
```
build.gradle.kts          # Main build configuration
settings.gradle.kts       # Project settings
gradle.properties         # Build properties
local.properties          # Local configuration (gitignored)
```

#### Common Gradle Tasks
```bash
# Clean build
./gradlew clean

# Compile Kotlin
./gradlew compileKotlin

# Run tests
./gradlew test

# Build fat JAR
./gradlew shadowJar

# Check dependencies
./gradlew dependencies
```

---

## 🔧 Adding New Features

### 1. Creating New API Endpoints

#### Step-by-Step Process
1. **Define Data Models**
   ```kotlin
   @Serializable
   data class User(
       val id: String = UUID.randomUUID().toString(),
       val name: String,
       val email: String
   )
   ```

2. **Create Service Layer**
   ```kotlin
   class UserService {
       private val users = mutableListOf<User>()
       private val mutex = Mutex()
       
       suspend fun getAllUsers(): List<User> = withContext(Dispatchers.Default) {
           mutex.withLock { users.toList() }
       }
   }
   ```

3. **Add Routing Configuration**
   ```kotlin
   private fun Routing.configureUserRouting(userService: UserService) {
       route("/users") {
           get {
               try {
                   val users = userService.getAllUsers()
                   call.respond(users)
               } catch (e: Exception) {
                   logger.error("Error getting users", e)
                   call.respond(HttpStatusCode.InternalServerError)
               }
           }
       }
   }
   ```

4. **Register in Main Routing**
   ```kotlin
   internal fun Application.configureRouting(
       // existing services...
       userService: UserService
   ) {
       routing {
           configureDefaultRouting()
           configureUserRouting(userService)
           // other routes...
       }
   }
   ```

### 2. Adding AI Tools

#### Creating Custom AI Tools
```kotlin
@Serializable
data class WeatherArgs(
    val city: String,
    val country: String? = null
)

class WeatherTool : AITool<WeatherArgs> {
    override val name = "get_weather"
    override val description = "Get current weather for a city"
    
    override suspend fun doExecute(args: WeatherArgs): String {
        // Implement weather API call
        return "Weather in ${args.city}: 22°C, Sunny"
    }
}
```

#### Registering Tools
```kotlin
val agent = AIAgent(
    executor = simpleOpenAIExecutor(apiKey),
    systemPrompt = SYSTEM_PROMPT,
    llmModel = OpenAIModels.Chat.GPT4_1,
    toolRegistry = ToolRegistry {
        tool(SayToUser)
        tool(PokemonInfoTool(pokemonApiService = pokemonService))
        tool(WeatherTool()) // Add your custom tool
    }
)
```

### 3. External API Integration

#### HTTP Client Setup
```kotlin
class ExternalApiService(private val baseUrl: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
        }
        
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
    }
    
    suspend fun getData(): Result<ApiResponse> = runCatching {
        client.get("$baseUrl/data").body()
    }
    
    fun close() {
        client.close()
    }
}
```

---

## 🧪 Testing Strategy

### Unit Testing Setup
```kotlin
// build.gradle.kts
testImplementation("io.ktor:ktor-server-test-host:3.2.0")
testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.1.20")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
```

### Testing Examples

#### Service Layer Testing
```kotlin
class TodoServiceTest {
    private val todoService = TodoService()
    
    @Test
    fun `should create new todo`() = runTest {
        val title = "Test todo"
        val result = todoService.addTodo(title)
        
        assertEquals(title, result.title)
        assertEquals(false, result.completed)
        assertNotNull(result.id)
    }
}
```

#### API Endpoint Testing
```kotlin
class RoutingTest {
    @Test
    fun `test root endpoint`() = testApplication {
        application {
            configureRouting(mockServices...)
        }
        
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello, Ktor! The server is running correctly!", bodyAsText())
        }
    }
}
```

---

## 🔍 Debugging Guide

### Logging Configuration

#### Logback Setup (`src/main/resources/logback.xml`)
```xml
<configuration>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <logger name="org.example" level="DEBUG"/>
    <logger name="io.ktor" level="INFO"/>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
    </root>
</configuration>
```

#### Application Logging
```kotlin
private val logger = LoggerFactory.getLogger("YourComponent")

// Use structured logging
logger.info("Processing request for user {}", userId)
logger.error("Failed to process request", exception)
logger.debug("Detailed debug information: {}", details)
```

### Common Debug Scenarios

#### 1. Port Conflicts
```kotlin
// Check if port finding is working
fun findAvailablePort(startPort: Int = 8080, endPort: Int = 9000): Int {
    for (port in startPort..endPort) {
        try {
            ServerSocket(port).use { socket ->
                socket.reuseAddress = true
                println("Port $port is available") // Debug output
                return port
            }
        } catch (e: Exception) {
            println("Port $port is in use: ${e.message}") // Debug output
        }
    }
    throw IllegalStateException("No available port found")
}
```

#### 2. JSON Serialization Issues
```kotlin
// Add debug logging to serialization
install(ContentNegotiation) {
    json(Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    })
}

// Log request/response bodies
install(CallLogging) {
    level = Level.INFO
    filter { call -> call.request.path().startsWith("/api") }
}
```

#### 3. AI Tool Debugging
```kotlin
class DebugPokemonInfoTool : AITool<PokemonInfoTool.Args> {
    override suspend fun doExecute(args: Args): String {
        logger.debug("Executing Pokemon tool with args: {}", args)
        
        return try {
            val result = pokemonService.getPokemonDetails(args.pokemonName)
            logger.debug("Pokemon API response: {}", result)
            result.getOrThrow().toString()
        } catch (e: Exception) {
            logger.error("Pokemon tool failed", e)
            throw e
        }
    }
}
```

---

## 🚀 Performance Optimization

### 1. HTTP Client Optimization
```kotlin
private val client = HttpClient {
    engine {
        // Connection pooling
        threadsCount = 4
        pipelining = true
    }
    
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 5_000
        socketTimeoutMillis = 30_000
    }
    
    // Response caching
    install(HttpCache)
}
```

### 2. Coroutine Optimization
```kotlin
// Use appropriate dispatchers
suspend fun cpuIntensiveTask() = withContext(Dispatchers.Default) {
    // CPU-bound work
}

suspend fun ioTask() = withContext(Dispatchers.IO) {
    // I/O operations
}

// Connection pooling for database operations
class DatabaseService {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    suspend fun batchOperation(items: List<Item>) {
        items.chunked(100).map { chunk ->
            scope.async { processChunk(chunk) }
        }.awaitAll()
    }
}
```

### 3. Memory Management
```kotlin
// Proper resource cleanup
class ServiceWithResources {
    private val client = HttpClient()
    private val scope = CoroutineScope(SupervisorJob())
    
    fun close() {
        client.close()
        scope.cancel()
    }
}
```

---

## 🔒 Security Best Practices

### 1. Environment Variable Management
```kotlin
// Instead of hardcoded API keys
private const val OPENAI_API_KEY = "sk-proj-..." // ❌ Bad

// Use environment variables
private val openaiApiKey = System.getenv("OPENAI_API_KEY") 
    ?: throw IllegalStateException("OPENAI_API_KEY not set") // ✅ Good
```

### 2. Input Validation
```kotlin
// Validate input parameters
private fun validateTodoTitle(title: String?): String {
    return title?.takeIf { it.isNotBlank() && it.length <= 255 }
        ?: throw IllegalArgumentException("Title must be non-empty and under 255 characters")
}
```

### 3. Error Handling
```kotlin
// Don't expose internal errors
try {
    // operation
} catch (e: Exception) {
    logger.error("Internal error processing request", e)
    call.respond(HttpStatusCode.InternalServerError, "An error occurred")
    // Don't include e.message in response
}
```

---

## 📦 Deployment Guide

### 1. Building for Production
```bash
# Create fat JAR
./gradlew shadowJar

# The JAR will be in build/libs/
java -jar build/libs/ktor-test-1.0-SNAPSHOT-all.jar
```

### 2. Docker Deployment
```dockerfile
FROM openjdk:21-jre-slim

WORKDIR /app
COPY build/libs/*-all.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
```

### 3. Environment Configuration
```bash
# Production environment variables
export OPENAI_API_KEY="your-api-key"
export LOG_LEVEL="INFO"
export SERVER_PORT="8080"
```

---

## 🎯 Best Practices Summary

### Code Organization
- ✅ Group related functionality in packages
- ✅ Separate concerns (models, services, routing)
- ✅ Use dependency injection
- ✅ Follow Kotlin conventions

### Error Handling
- ✅ Use Result types for external operations
- ✅ Log errors with context
- ✅ Provide meaningful error responses
- ✅ Don't expose internal errors to clients

### Performance
- ✅ Use appropriate coroutine dispatchers
- ✅ Implement connection pooling
- ✅ Clean up resources properly
- ✅ Monitor and profile performance

### Security
- ✅ Use environment variables for secrets
- ✅ Validate all inputs
- ✅ Don't expose internal error details
- ✅ Implement rate limiting for production

---

*For complete API documentation, see [API_REFERENCE.md](API_REFERENCE.md)*  
*For project overview, see [PROJECT_INDEX.md](PROJECT_INDEX.md)*