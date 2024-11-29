plugins {
    id("java")
}

group = "pl.cezarysanecki"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    val springVersion = "6.2.0"

    implementation("org.springframework:spring-core:${springVersion}")
    implementation("org.springframework:spring-context:${springVersion}")
    implementation("org.springframework:spring-jdbc:${springVersion}")

    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.2")

    testImplementation("org.assertj:assertj-core:3.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.springframework:spring-test:${springVersion}")
}

tasks.test {
    useJUnitPlatform()
}