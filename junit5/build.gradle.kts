import com.adarshr.gradle.testlogger.TestLoggerExtension

plugins {
    kotlin("jvm")
    id("com.adarshr.test-logger") version "1.3.0"
}

configure<TestLoggerExtension> {
    setTheme("mocha")
}

dependencies {
    testCompile(kotlin("stdlib-jdk8"))

    testCompile("net.wuerl.kotlin:assertj-core-kotlin:0.2.1")
    testCompile("org.assertj:assertj-core:3.10.0")

    testImplementation(
            "org.junit.jupiter:junit-jupiter-api:5.2.0"
    )
    testRuntimeOnly(
            "org.junit.jupiter:junit-jupiter-engine:5.2.0"
    )
}

tasks.withType(Test::class.java) {
    useJUnitPlatform {
        includeEngines = setOf("junit-jupiter")
    }
}
