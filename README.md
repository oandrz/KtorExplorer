# Ktor Test Application

A simple Ktor web application with automatic port selection and logging.

## Features

- Automatic port selection (finds first available port between 8080-9000)
- Basic request/response logging
- JSON content negotiation
- Simple error handling

## Requirements

- Java 21+
- Gradle 8.0+

## Running the Application

1. Clone the repository
2. Run the application:
   ```bash
   ./gradlew run
   ```
3. Open your browser to the URL shown in the console (typically http://localhost:8080)

## Endpoints

- `GET /` - Returns a welcome message
- `GET /ai` - AI endpoint (requires OPENAI_API_KEY environment variable)

## Configuration

Set the following environment variables if needed:
- `OPENAI_API_KEY` - Required for the /ai endpoint

## Logging

Logs are output to the console with timestamps. The log level can be configured in `src/main/resources/logback.xml`.
