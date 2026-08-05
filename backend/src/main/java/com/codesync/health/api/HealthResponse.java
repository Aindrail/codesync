package com.codesync.health.api;

/*
Why a Record?

Java 21 gives us Records.

Instead of writing

getters
constructor
equals
hashCode
toString

Java generates everything automatically.

They're perfect for immutable API responses.
 */
public record HealthResponse(
        String application,
        String version,
        String status,
        java.time.Instant timestamp
) {
}
