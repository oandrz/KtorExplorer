# API Reference - Ktor Test Project

## Base URL
```
http://localhost:{dynamic-port}
```
> Port is dynamically allocated between 8080-9000. Check console output for actual port.

## 🏥 Health & Status Endpoints

### Health Check
```http
GET /
```

**Response**
```
Status: 200 OK
Content-Type: text/plain

"Hello, Ktor! The server is running correctly!"
```

---

## 📝 TODO Management API

### List All TODOs
```http
GET /todos
```

**Response**
```json
Status: 200 OK
Content-Type: application/json

[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "title": "Buy groceries",
    "completed": false,
    "createdAt": 1704067200000
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "title": "Complete project",
    "completed": true,
    "createdAt": 1704067260000
  }
]
```

### Get TODO by ID
```http
GET /todos/{id}
```

**Parameters**
- `id` (path) - TODO UUID

**Response - Success**
```json
Status: 200 OK
Content-Type: application/json

{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Buy groceries",
  "completed": false,
  "createdAt": 1704067200000
}
```

**Response - Not Found**
```json
Status: 404 Not Found
Content-Type: text/plain

"Todo not found"
```

### Create New TODO
```http
POST /todos
Content-Type: application/json
```

**Request Body**
```json
{
  "title": "New task description"
}
```

**Response - Success**
```json
Status: 201 Created
Content-Type: application/json

{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "title": "New task description",
  "completed": false,
  "createdAt": 1704067320000
}
```

**Response - Bad Request**
```json
Status: 400 Bad Request
Content-Type: text/plain

"Title is required"
```

### Toggle TODO Completion
```http
PUT /todos/{id}/toggle
```

**Parameters**
- `id` (path) - TODO UUID

**Response - Success**
```json
Status: 200 OK
Content-Type: application/json

{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "title": "Buy groceries",
  "completed": true,
  "createdAt": 1704067200000
}
```

### Delete TODO
```http
DELETE /todos/{id}
```

**Parameters**
- `id` (path) - TODO UUID

**Response - Success**
```
Status: 204 No Content
```

**Response - Not Found**
```json
Status: 404 Not Found
Content-Type: text/plain

"Todo not found"
```

---

## 📖 Blog API

### List Blog Posts
```http
GET /blog/list
```

**Response**
```json
Status: 200 OK
Content-Type: application/json

[
  {
    "id": "blog-post-1",
    "title": "Getting Started with Ktor",
    "content": "Ktor is a framework for building asynchronous servers...",
    "author": "oink",
    "createdAt": 1704067200000
  }
]
```

### Create Blog Post
```http
POST /blog/post
Content-Type: application/x-www-form-urlencoded
```

**Request Body**
```
title=My Blog Post Title&content=This is the content of my blog post
```

**Response - Success**
```json
Status: 201 Created
Content-Type: text/plain

"Blog post created successfully"
```

**Response - Bad Request**
```json
Status: 400 Bad Request
Content-Type: text/plain

"Title is required"
```

---

## 🤖 AI Integration API

### AI Endpoint Information
```http
GET /ai
```

**Response**
```json
Status: 200 OK
Content-Type: application/json

"AI endpoint is live. Use POST /ai/query with form parameter 'userPrompt' to query the AI."
```

### AI Query Processing
```http
POST /ai/query
Content-Type: application/x-www-form-urlencoded
```

**Request Body**
```
userPrompt=Tell me about Pikachu's abilities and characteristics
```

**Response - Success**
```json
Status: 200 OK
Content-Type: application/json

{
  "response": "Pikachu is an Electric-type Pokémon...",
  "toolsUsed": ["PokemonInfoTool"],
  "processingTime": 1234
}
```

**Available AI Capabilities**
- Pokemon information lookup via PokemonInfoTool
- General conversation and assistance
- JSON-only response format
- Tool integration for enhanced responses

---

## 📊 Response Codes & Error Handling

### Standard HTTP Status Codes

**Success Responses**
- `200 OK` - Request successful
- `201 Created` - Resource created successfully
- `204 No Content` - Successful deletion

**Client Error Responses**
- `400 Bad Request` - Invalid request parameters
- `404 Not Found` - Resource not found

**Server Error Responses**
- `500 Internal Server Error` - Server processing error

### Error Response Format

**Standard Error Response**
```json
{
  "error": "Descriptive error message",
  "timestamp": 1704067200000,
  "path": "/todos/invalid-id"
}
```

**Plain Text Errors**
Some endpoints return plain text error messages for simplicity:
```
"Error message description"
```

---

## 🔧 Request/Response Details

### Content Types

**Accepted Request Types**
- `application/json` - For TODO operations
- `application/x-www-form-urlencoded` - For blog posts and AI queries
- `text/plain` - For simple requests

**Response Content Types**
- `application/json` - Structured data responses
- `text/plain` - Simple text responses

### Headers

**Common Request Headers**
```http
Content-Type: application/json
Accept: application/json
```

**Common Response Headers**
```http
Content-Type: application/json; charset=utf-8
```

### Performance Characteristics

**Simulated Delays** (for demonstration purposes)
- TODO operations: 300-500ms
- Pokemon API calls: Real network latency
- AI processing: Variable based on complexity

---

## 🧪 Example Usage

### Complete TODO Workflow
```bash
# 1. List all todos
curl -X GET http://localhost:8081/todos

# 2. Create a new todo
curl -X POST http://localhost:8081/todos \
  -H "Content-Type: application/json" \
  -d '{"title": "Learn Ktor framework"}'

# 3. Toggle completion status
curl -X PUT http://localhost:8081/todos/{id}/toggle

# 4. Delete the todo
curl -X DELETE http://localhost:8081/todos/{id}
```

### AI Integration Example
```bash
# Query AI about Pokemon
curl -X POST http://localhost:8081/ai/query \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'userPrompt=What are the characteristics of Charizard?'
```

### Blog Operations
```bash
# Create blog post
curl -X POST http://localhost:8081/blog/post \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d 'title=My First Post&content=This is my blog content'

# List all posts
curl -X GET http://localhost:8081/blog/list
```

---

## 🔐 Authentication & Security

### Current Status
- **No Authentication Required** - All endpoints are publicly accessible
- **No Rate Limiting** - Unlimited requests (development setup)
- **No CORS Configuration** - May require CORS setup for browser clients

### Production Considerations
- Add API key authentication
- Implement rate limiting
- Configure CORS policies
- Add request validation
- Enable HTTPS/TLS

---

*For complete project documentation, see [PROJECT_INDEX.md](PROJECT_INDEX.md)*