plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh") version "0.5.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":"))
    testAnnotationProcessor("org.openjdk.jmh:jmh-core:1.22")
    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.22")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    task<Exec>("plot") {
        workingDir = File("Graph")
        commandLine("python", "graph.py")
    }
    "jmh" {
        finalizedBy("plot")
    }
}
