import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.8.22"
  kotlin("plugin.spring") version "1.8.22"
  kotlin("plugin.jpa") version "1.8.22"
  id("org.springframework.boot") version "3.1.5"
}

group = "ch.barrierelos"
version = "0.0.0-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.5")
  implementation("org.springframework.boot:spring-boot-starter-web:3.1.5")
  implementation("org.yaml:snakeyaml:2.2")

  runtimeOnly("org.postgresql:postgresql:42.6.0")

}

kotlin {
  explicitApi()
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs += "-Xjsr305=strict"
    jvmTarget = "17"
  }
}
