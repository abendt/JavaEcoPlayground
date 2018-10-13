import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.bmuschko.gradle.docker.tasks.DockerInfo
import com.bmuschko.gradle.docker.tasks.DockerVersion
import com.bmuschko.gradle.docker.tasks.container.DockerCreateContainer
import com.bmuschko.gradle.docker.tasks.container.DockerLogsContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStartContainer
import com.bmuschko.gradle.docker.tasks.container.DockerStopContainer
import com.bmuschko.gradle.docker.tasks.container.extras.DockerWaitHealthyContainer
import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.DockerPullImage


plugins {
    kotlin("jvm")

    id("org.springframework.boot") version "2.0.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"

    id("org.jetbrains.kotlin.plugin.spring") version "1.2.71"
    id("com.bmuschko.docker-remote-api") version "3.6.2"
}

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))

    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.7")

    testCompile(kotlin("test-junit"))
    testCompile("net.wuerl.kotlin:assertj-core-kotlin:0.2.1")
    testCompile("org.assertj:assertj-core:3.11.1")
    testCompile("com.nhaarman:mockito-kotlin-kt1.1:1.6.0")
    testCompile("io.rest-assured:rest-assured:3.1.1")
    testCompile("org.awaitility:awaitility:3.1.2")

    testRuntime(kotlin("reflect"))

    compile("org.springframework.boot:spring-boot-starter-websocket")
    compile(group = "io.projectreactor.ipc", name = "reactor-netty", version = "0.7.10.RELEASE")
    compile("io.netty:netty-all:4.1.30.Final")
    compile("org.webjars:webjars-locator-core:0.35")
    compile("org.webjars:sockjs-client:1.1.2")
    compile("org.webjars:stomp-websocket:2.3.3-1")
    compile("org.webjars:bootstrap:4.1.3")
    compile("org.webjars:jquery:3.3.1-1")
    compile("io.github.microutils:kotlin-logging:1.6.10")

    testCompile("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    val testContainerName = "websockets-activemq"

    val dockerInfo by creating(DockerInfo::class) {}

    val dockerVersion by creating(DockerVersion::class) {}

    val dockerRemove by creating(Exec::class) {
        group = "docker"
        executable = "docker"
        setArgs(listOf("rm", "-f", testContainerName))
        isIgnoreExitValue = true
    }

    val dockerCreate by creating(DockerCreateContainer::class) {
        dependsOn("dockerRemove")
        targetImageId { "rmohr/activemq:5.15.6-alpine" }

        portBindings = listOf("61613:61613", "8161:8161")
        containerName = testContainerName
    }

    val dockerStart by creating(DockerStartContainer::class) {
        dependsOn(dockerCreate)

        targetContainerId { dockerCreate.containerId }
    }

    val dockerStop by creating(DockerStopContainer::class) {
        targetContainerId { dockerCreate.containerId }
    }

    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}