import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        jcenter()
    }
}

plugins {
    base
    kotlin("jvm") version "1.2.31" apply false
}

allprojects {
    repositories {
        jcenter()
    }

    tasks.withType(KotlinCompile::class.java).all {
        kotlinOptions {
           jvmTarget = "1.8"
        }
    }
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
