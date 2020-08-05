plugins {
    kotlin("jvm")
    id("me.champeau.gradle.jmh") version "0.5.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(":"))
    implementation("it.unimi.dsi:fastutil:8.2.1")
    implementation("org.openjdk.jol:jol-core:0.11")
}
