import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.9.20"
  kotlin("plugin.serialization") version "1.9.20"
  kotlin("plugin.spring") version "1.8.22"
  kotlin("plugin.jpa") version "1.8.22"
  id("org.springframework.boot") version "3.1.5"
  id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
}

group = "ch.barrierelos"
version = "0.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.10")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
  implementation("org.springframework.amqp:spring-rabbit:3.0.10")
  implementation("org.springframework.boot:spring-boot-starter-amqp:3.1.5")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.5")
  implementation("org.springframework.boot:spring-boot-starter-security:3.1.5")
  implementation("org.springframework.boot:spring-boot-starter-web:3.1.5")
  implementation("org.springframework.security:spring-security-oauth2-jose:6.1.5")
  implementation("org.springframework.security:spring-security-oauth2-resource-server:6.1.5")
  implementation("org.yaml:snakeyaml:2.2")
  implementation("me.paulschwarz:spring-dotenv:4.0.0")
  implementation("org.flywaydb:flyway-core:9.16.3")
  implementation(kotlin("script-runtime"))

  runtimeOnly("org.postgresql:postgresql:42.6.0")

  testImplementation("com.h2database:h2:2.2.224")
  testImplementation("com.ninja-squad:springmockk:4.0.2")
  testImplementation("io.kotest:kotest-assertions-core-jvm:5.8.0")
  testImplementation("io.mockk", "mockk", "1.13.8")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
  testImplementation("org.springframework.boot", "spring-boot-starter-test", "3.1.5") {
    exclude("org.junit.vintage", "junit-vintage-engine")
    exclude("org.mockito", "mockito-core")
  }
  testImplementation("org.springframework.security:spring-security-test:6.1.5")

  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
}

kotlin {
  explicitApiWarning()
}

openApi {
  apiDocsUrl.set("http://localhost:8030/openapi")
  outputDir.set(layout.buildDirectory.dir("openapi").get().asFile)
  outputFileName.set("openapi.json")
}

tasks {
  withType<KotlinCompile> {
    kotlinOptions {
      freeCompilerArgs += "-Xjsr305=strict"
      jvmTarget = "17"
    }
  }

  getByName<Jar>("jar") {
    enabled = false
  }

  test {
    useJUnitPlatform()
  }
}

task<Exec>("dockerStop") {
  group = "docker"
  description = "Stops this projects Docker container."

  commandLine("docker", "stop", "barrierelos-backend")

  setIgnoreExitValue(true)
}

task<Exec>("dockerRemove") {
  group = "docker"
  description = "Stops this projects Docker container."

  dependsOn("dockerStop")

  commandLine("docker", "rm", "barrierelos-backend")

  setIgnoreExitValue(true)
}

task<Exec>("dockerBuild") {
  group = "docker"
  description = "Creates a Docker image for this project."

  dependsOn("build", "dockerRemove")

  commandLine("docker", "build", "--tag", "ch/barrierelos/backend", ".")
}

task<Exec>("dockerCleanup") {
  group = "docker"
  description = "Removes all dangling images of this project."

  dependsOn("dockerBuild")

  commandLine("docker", "image", "prune", "--force")
}

task<Exec>("dockerUp") {
  group = "docker"
  description = "Ups this project as a Docker container."

  dependsOn("dockerCleanup")

  commandLine("docker", "compose", "up", "-d")
}

task<Exec>("dockerDependenciesUp") {
  group = "docker"
  description = "Launches this application's Docker dependencies using docker compose"

  commandLine(
    "sh",
    "-c",
    "docker compose -f compose-prod.yml -f compose-dev.yml up -d --scale backend=0"
  )
}

task("dockerRun") {
  group = "docker"
  description = "Runs this project as a Docker container."

  dependsOn("dockerUp")
}
