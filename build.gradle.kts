import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("jacoco")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

allprojects {
    group = "com.gigcred"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "jacoco")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
        withSourcesJar()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events = setOf(TestLogEvent.FAILED)
        }
    }

    extensions.configure(JacocoPluginExtension::class.java) {
        toolVersion = "0.8.11"
    }

    tasks.withType<JacocoReport> {
        reports {
            xml.required.set(true)
            csv.required.set(false)
            html.required.set(true)
        }
    }
}

project(":common:common-domain") {
    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.1"))
        testImplementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework:spring-context")
        implementation("com.fasterxml.jackson.core:jackson-databind")
        testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    }
}

project(":common:common-data") {
    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.1"))
        annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        testImplementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        implementation(project(":common:common-domain"))
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.springframework.boot:spring-boot-starter-json")
        runtimeOnly("org.postgresql:postgresql")
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("com.h2database:h2")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
    }
}

fun Project.configureServiceModule(vararg extraDependencies: Any) {
    dependencies {
        implementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        implementation(platform("org.springframework.cloud:spring-cloud-dependencies:2023.0.1"))
        annotationProcessor(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        testImplementation(platform("org.springframework.boot:spring-boot-dependencies:3.3.1"))
        implementation(project(":common:common-domain"))
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("org.springframework.boot:spring-boot-starter-security")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.kafka:spring-kafka")
        implementation("io.github.resilience4j:resilience4j-spring-boot3")
        implementation("org.mapstruct:mapstruct:1.5.5.Final")
        annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
        extraDependencies.forEach { add("implementation", it) }
        runtimeOnly("org.postgresql:postgresql")
        implementation("org.springframework.boot:spring-boot-starter-cache")
        implementation("org.springframework.boot:spring-boot-starter-aop")
        implementation("org.springframework.boot:spring-boot-starter-json")
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.5.0")
        testImplementation("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        }
        testImplementation("org.springframework.kafka:spring-kafka-test")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:kafka")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:redis")
        testImplementation("org.mockito:mockito-core:5.11.0")
        testImplementation("org.assertj:assertj-core:3.25.3")
    }
}

project(":integrations:flutterwave-adapter") {
    configureServiceModule(
        "org.springframework.cloud:spring-cloud-starter-openfeign",
        "org.springframework.boot:spring-boot-starter-cache"
    )
    dependencies {
        implementation("org.springframework.boot:spring-boot-configuration-processor")
        annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
        testImplementation("com.github.tomakehurst:wiremock-jre8:3.0.1")
    }
}

listOf(
    ":services:gateway-service",
    ":services:onboarding-service",
    ":services:accounts-service",
    ":services:cards-service",
    ":services:payments-service",
    ":services:ledger-service",
    ":services:scoring-service",
    ":services:loans-service",
    ":services:compliance-service",
    ":services:notifications-service",
    ":services:ops-service"
).forEach { path ->
    project(path) {
        configureServiceModule(project(":integrations:flutterwave-adapter"))
    }
}

configurations.all {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
}

subprojects {
    dependencies {
        implementation("ch.qos.logback:logback-classic:1.5.6")
        implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    }
}
