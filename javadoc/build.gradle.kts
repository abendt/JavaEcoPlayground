import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.17")
    }
}

plugins {
    kotlin("jvm")
}

apply {
    plugin("org.jetbrains.dokka")
}

tasks {
    val dokkaJavadoc by creating(DokkaTask::class) {
        outputFormat = "javadoc"
        outputDirectory = "$buildDir/dokkaJavadoc"
    }
}
