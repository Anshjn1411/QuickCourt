plugins {
    kotlin("jvm") version "2.0.20" // keep stable, not 2.2.0 (too new)
    id("application")
    kotlin("plugin.serialization") version "2.2.0" // 👈 this enables ./gradlew run
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("io.ktor:ktor-server-core-jvm:2.3.12")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.12")

    // ✅ JSON serialization
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.12")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.12")

    // WebSockets
    implementation("io.ktor:ktor-server-websockets-jvm:2.3.12")

    // Kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation(kotlin("test"))


    implementation("io.ktor:ktor-server-websockets:2.3.5")
    // ✅ Ktor Server
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-client-core:2.3.5")
    implementation("io.ktor:ktor-client-cio:2.3.5")
    implementation("io.ktor:ktor-client-websockets:2.3.5")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

    // ✅ JSON Support
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")

    // ✅ Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")

    // ✅ Testing
    testImplementation("io.ktor:ktor-server-tests:2.3.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.23")
}

application {
    // 👇 point this to where your main() is
    mainClass.set("org.example.MainKt")
}
