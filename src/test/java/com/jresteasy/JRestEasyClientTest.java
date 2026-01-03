package com.jresteasy;

import com.jresteasy.client.JRest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JRestEasyClientTest {

    // Using a public API for demonstration: https://jsonplaceholder.typicode.com

    @Test
    void testSimpleGet() {
        // 1. Simple GET as String
        String response = JRest.get("https://jsonplaceholder.typicode.com/todos/1")
                .asString();

        assertNotNull(response);
        assertTrue(response.contains("userId"));
    }

    @Test
    void testJsonPath() {
        // 2. Extract specific field without POJO
        String title = JRest.get("https://jsonplaceholder.typicode.com/todos/1")
                .path("$.title");

        System.out.println("Extracted Title: " + title);
        assertNotNull(title);
    }

    @Test
    void testPostWithBody() {
        // 3. POST with Map body
        Map<String, Object> newTodo = Map.of(
                "title", "foo",
                "body", "bar",
                "userId", 1);

        Map response = JRest.post("https://jsonplaceholder.typicode.com/posts")
                .body(newTodo)
                .as(Map.class);

        assertEquals("foo", response.get("title"));
    }

    @Test
    void testRetryLogic() {
        // 4. Test Retry (will fail eventually, but shows logic)
        // We expect this to fail, but we want to see it retry in logs if debug is on
        assertThrows(RuntimeException.class, () -> {
            JRest.get("https://invalid-url-that-does-not-exist.com")
                    .retry(2)
                    .timeout(1000)
                    .debug()
                    .asString();
        });
    }
}
