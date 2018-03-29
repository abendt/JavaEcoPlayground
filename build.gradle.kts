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
}

dependencies {
    // Make the root project archives configuration depend on every subproject
    subprojects.forEach {
        archives(it)
    }
}
