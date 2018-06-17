import org.jetbrains.kotlin.gradle.dsl.Coroutines

plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("com.netflix.hystrix:hystrix-core:1.5.13")
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-rx1:0.23.3")
    testCompile(kotlin("reflect"))
    testCompile(kotlin("test-junit"))
    testCompile("net.wuerl.kotlin:assertj-core-kotlin:0.2.1")
    testCompile("org.assertj:assertj-core:3.10.0")
    testCompile("junit:junit:4.12")

    testRuntime(kotlin("reflect"))
}

kotlin { // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}