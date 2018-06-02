import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:1.5.13.RELEASE")
    }
}

plugins {
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.spring") version "1.2.41"
    id("org.springframework.boot") version "1.5.13.RELEASE"
    id("io.spring.dependency-management") version "1.0.5.RELEASE"
}

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.5")

    testCompile(kotlin("test-junit"))
    testCompile("net.wuerl.kotlin:assertj-core-kotlin:0.2.1")
    testCompile("org.assertj:assertj-core:3.10.0")
    testCompile("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
    testCompile("io.rest-assured:rest-assured:3.1.0")
    testCompile("org.awaitility:awaitility:3.1.0")

    testRuntime(kotlin("reflect"))

    compile("org.springframework.boot:spring-boot-starter-websocket")
    compile("org.webjars:webjars-locator-core:0.35")
    compile("org.webjars:sockjs-client:1.0.2")
    compile("org.webjars:stomp-websocket:2.3.3")
    compile("org.webjars:bootstrap:3.3.7")
    compile("org.webjars:jquery:3.1.0")
    compile("io.github.microutils:kotlin-logging:1.5.4")

    testCompile("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}