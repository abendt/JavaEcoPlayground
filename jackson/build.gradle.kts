plugins {
    kotlin("jvm")
    application
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.5")

    compile("io.github.microutils:kotlin-logging:1.5.4")

    compile("org.reflections:reflections:0.9.11")

    runtime("ch.qos.logback:logback-classic:1.2.3")

    testCompile(kotlin("reflect"))
    testCompile(kotlin("test-junit"))
    testCompile("net.wuerl.kotlin:assertj-core-kotlin:0.2.1")
    testCompile("org.assertj:assertj-core:3.9.1")
    testCompile("junit:junit:4.12")

    testRuntime(kotlin("reflect"))
}