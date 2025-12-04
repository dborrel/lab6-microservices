plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    id("org.openapi.generator") version "7.2.0"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform(libs.spring.boot.bom))
    implementation(platform(libs.spring.cloud.bom))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.thymeleaf)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.cloud.starter.netflix.eureka.client)
    implementation(libs.spring.cloud.starter.config)

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Circuit Breaker
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("io.github.resilience4j:resilience4j-micrometer")
    implementation("io.micrometer:micrometer-registry-prometheus")
    
    // OpenAPI Generator dependencies
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("jakarta.annotation:jakarta.annotation-api:2.1.1")

    // Testing dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

openApiGenerate {
    generatorName.set("java")
    inputSpec.set("$projectDir/web-api.json")
    outputDir.set(layout.buildDirectory.dir("generated").get().asFile.path)
    apiPackage.set("web.client.api")
    modelPackage.set("web.client.model")
    configOptions.set(mapOf(
        "dateLibrary" to "java8",
        "library" to "resttemplate",
        "useSpringBoot3" to "true",
        "useJakartaEe" to "true"
    ))
}

sourceSets {
    main {
        java {
            srcDir(layout.buildDirectory.dir("generated/src/main/java"))
        }
    }
}

tasks.named("compileJava") {
    dependsOn("openApiGenerate")
}

tasks.test {
    useJUnitPlatform()
    dependsOn("openApiGenerate")
    
    testLogging {
        events("passed", "skipped", "failed")
        showStandardStreams = true
    }
}
