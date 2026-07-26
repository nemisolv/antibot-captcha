plugins {
    java
    id("org.springframework.boot") version "4.1.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.viettel"
version = "0.0.1-SNAPSHOT"
description = "captcha"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-restclient")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    developmentOnly ("org.springframework.boot:spring-boot-devtools")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.4")
    implementation("com.github.penggle:kaptcha:2.3.2")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
