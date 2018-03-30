plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "demo.DynamicLoggingDemoKt"
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("io.github.microutils:kotlin-logging:1.5.4")

    runtime("ch.qos.logback:logback-classic:1.2.3")

    testCompile(kotlin("reflect"))
    testCompile(kotlin("test-junit"))

    testCompile("junit:junit:4.12")

    testRuntime(kotlin("reflect"))
}