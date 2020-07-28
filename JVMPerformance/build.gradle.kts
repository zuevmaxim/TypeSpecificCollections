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
    implementation(project(":Performance"))
    implementation("it.unimi.dsi:fastutil:8.2.1")
}
