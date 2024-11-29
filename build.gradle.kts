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
    implementation("org.springframework:spring-jdbc:${springVersion}")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}