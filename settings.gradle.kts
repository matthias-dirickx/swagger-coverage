rootProject.name = "swagger-coverage"

pluginManagement {
    plugins {
        id("io.spring.dependency-management") version "1.0.11.RELEASE"
    }
}

include("swagger-coverage-commandline")
include("swagger-coverage-rest-assured")
include("swagger-coverage-commons")
include("swagger-coverage-karate")
include("swagger-coverage-okhttp3")
