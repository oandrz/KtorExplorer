plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    id("io.ktor.plugin") version "3.2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:3.2.0")
    implementation("io.ktor:ktor-server-netty-jvm:3.2.0")
    implementation("io.ktor:ktor-server-auth:3.2.0")
    implementation("io.ktor:ktor-server-auth-jwt:3.2.0")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:3.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:3.2.0")

    // JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.8.1")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Kotlin DateTime (fix NoClassDefFoundError: kotlinx/datetime/Clock$System)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    // Ensure the correct version is present on the runtime classpath
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")

    // AI Agents
    implementation("ai.koog:koog-agents:0.4.1") {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-datetime")
    }

    // Arrow-kt (Functional Programming)
    implementation(platform("io.arrow-kt:arrow-stack:1.2.1"))
    implementation("io.arrow-kt:arrow-core")
    implementation("io.arrow-kt:arrow-fx-coroutines")
    implementation("io.arrow-kt:arrow-optics")

    implementation("org.mindrot:jbcrypt:0.4")


    // Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:3.2.2"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.github.jan-tennert.supabase:auth-kt")
    implementation("io.github.jan-tennert.supabase:realtime-kt")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // Enforce kotlinx-datetime version across all configurations
    constraints {
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0") {
            because("Prevent older transitive versions causing NoClassDefFoundError: kotlinx/datetime/Clock System")
        }
    }

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("org.example.MainKt")
}

// Globally force the correct version of kotlinx-datetime to avoid runtime NoClassDefFoundError
configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    }
}