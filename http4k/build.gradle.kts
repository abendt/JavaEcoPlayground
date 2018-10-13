import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm")
}

fun http4k(name: String) = "org.http4k:http4k-$name:3.38.1"

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile(http4k("core"))
    compile(http4k("client-okhttp"))
    compile(http4k("resilience4j"))

    testCompile(kotlin("test-junit"))
    testCompile("io.strikt:strikt-core:0.16.0")
    testCompile("junit:junit:4.12")
    testCompile("org.awaitility:awaitility:3.1.2")
    testCompile("com.github.tomakehurst:wiremock:2.19.0")

    testRuntime("ch.qos.logback:logback-classic:1.2.3")
    testRuntime(kotlin("reflect"))
}

kotlin {
    // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}