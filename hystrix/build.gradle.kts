plugins {
    kotlin("jvm")
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("com.netflix.hystrix:hystrix-core:1.5.13")

    testCompile(kotlin("reflect"))
    testCompile(kotlin("test-junit"))
    testCompile("net.wuerl.kotlin:assertj-core-kotlin:0.2.1")
    testCompile("org.assertj:assertj-core:3.9.1")
    testCompile("junit:junit:4.12")

    testRuntime(kotlin("reflect"))
}