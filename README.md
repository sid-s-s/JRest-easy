# JRest-easy: The Developer-First HTTP Client

**JRest-easy** is a lightweight, fluent wrapper around Spring's `RestClient` designed to make HTTP requests in Java simple, readable, and robust.

## Why JRest-easy?

Standard Java HTTP clients (`WebClient`, `RestClient`, `RestTemplate`) are powerful but often verbose. They require boilerplate for simple tasks like error handling, retries, and JSON parsing.

**JRest-easy** solves this with a fluent API that focuses on the *intent* of your code, not the plumbing.

## Features

- 🚀 **Fluent API**: Write requests in a single readable chain.
- 🛡️ **Built-in Resilience**: Simple `.retry(3)` and `.timeout(5000)` methods.
- 🔍 **JSON Path Support**: Extract fields directly without creating POJOs.
- ⚡ **Auto-Magic Error Handling**: Throws unified `JRestException` for 4xx/5xx errors.
- 🐞 **Debug Mode**: See exactly what's happening with `.debug()`.

## Installation

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>com.jresteasy</groupId>
    <artifactId>jrest-easy-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Usage Examples

### 1. Simple GET
```java
// Get a POJO
User user = JRest.get("https://api.example.com/users/1")
                 .as(User.class);

// Get a String
String json = JRest.get("https://api.example.com/users/1")
                   .asString();
```

### 2. POST with Headers & Body
```java
UserRequest newUser = new UserRequest("John", "john@example.com");

User createdUser = JRest.post("https://api.example.com/users")
                        .header("Authorization", "Bearer token")
                        .body(newUser)
                        .as(User.class);
```

### 3. Extract Data without POJOs (JSON Path)
```java
// Extract just the email from a deep JSON structure
String email = JRest.get("https://api.example.com/users/1")
                    .path("$.contact.details.email");
```

### 4. Retries & Timeout
```java
JRest.get("https://unstable-api.com/data")
     .retry(3)       // Retry 3 times if it fails
     .timeout(2000)  // 2 second timeout per attempt
     .asString();
```

## Requirements
- Java 17+
- Spring Boot 3.2+

## License
MIT
