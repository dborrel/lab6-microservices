plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.spring.cloud.bom))
    implementation(libs.kotlin.reflect)

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.data:spring-data-commons")
    implementation("org.hsqldb:hsqldb")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    implementation(libs.spring.cloud.starter.netflix.eureka.client)
    implementation(libs.spring.cloud.starter.config)
    implementation(libs.google.gson)
}
