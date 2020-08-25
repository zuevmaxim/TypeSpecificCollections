plugins {
    kotlin("multiplatform")
    id("kotlinx.benchmark") version "0.2.0-dev-8"
}

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/kotlinx")
}

kotlin {
    val kotlinxBenchmarkVersion = "0.2.0-dev-8"
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation(project(":"))
                implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-metadata:$kotlinxBenchmarkVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        js {
            nodejs()
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("stdlib-js"))
                    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-js:$kotlinxBenchmarkVersion")
                }
            }
            compilations["test"].defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
        macosX64 {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-macosx64:$kotlinxBenchmarkVersion")
                }
            }
        }
        linuxX64 {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx.benchmark.runtime-linuxx64:$kotlinxBenchmarkVersion")
                }
            }
        }
    }
}

benchmark {
    targets {
        register("js")
        register("macosX64")
        register("linuxX64")
    }
}
