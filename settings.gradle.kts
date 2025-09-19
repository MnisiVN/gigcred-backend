pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://repo.spring.io/release") }
    }
}

rootProject.name = "gigcred-backend"

include(
    "common:common-domain",
    "common:common-data",
    "integrations:flutterwave-adapter",
    "services:gateway-service",
    "services:onboarding-service",
    "services:accounts-service",
    "services:cards-service",
    "services:payments-service",
    "services:ledger-service",
    "services:scoring-service",
    "services:loans-service",
    "services:compliance-service",
    "services:notifications-service",
    "services:ops-service"
)
