plugins {
    java
    `java-library`
}

description = "Swagger Coverage OKHTTP 3"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":swagger-coverage-commons"))
    api("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("io.swagger:swagger-models")
    implementation("io.swagger.core.v3:swagger-models")
    testImplementation("junit:junit")
    testImplementation("com.github.tomakehurst:wiremock")
    testImplementation("org.hamcrest:hamcrest")
}

tasks {
    test {
        //set the workingDir to the build dir so we don't pollute the main project dir
        //with generated test files
        workingDir(buildDir)
    }
}